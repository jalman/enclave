package team059.soldiers;

import team059.RobotBehavior;
import team059.movement.Mover;
import team059.messaging.MessageHandler;
import team059.soldiers.micro.Micro;
import team059.utils.Utils;
import battlecode.common.*;
import team059.soldiers.mineLay.MineLayer;
import static team059.soldiers.SoldierMode.*;

public class SoldierBehavior extends RobotBehavior {

	private SoldierMode mode;
	private MapLocation target = null;
	private int priority;
	private MapLocation gather;
	private boolean charging = false;
	private Micro microSystem;
	private MapLocation c = null, p = null; // c = current location, p = past location.
	
	GameObject[] enemies = new GameObject[0], allies = new GameObject[0];
	RobotInfo[] enemySoldiers = new RobotInfo[0], alliedSoldiers = new RobotInfo[0];
	
	
	public final Mover mover;
	public final MineLayer mineLayer;
	
	public SoldierBehavior(RobotController therc) throws GameActionException {
		super(therc);
		c = rc.getLocation();
		p = rc.getLocation();
		mover = new Mover((RobotBehavior) this);
		mineLayer = new MineLayer(rc);
		gather = new MapLocation((myBase.x * 3 + enemyBase.x * 2) / 5, (myBase.y * 3 + enemyBase.y * 2) / 5); //remove when micro works
		mode = SoldierMode.IDLE; // for now
		microSystem = new Micro(this);
	}

	@Override
	public void run() {
		if(!rc.isActive()) return;

		try {
			messagingSystem.readMessages();
		} catch (GameActionException e1) {
			e1.printStackTrace();
		}

		messagingSystem.handleMessages(messageHandlers);
		considerSwitchingModes();

		try {
			switch(mode) {
			case IDLE:
				idleBehavior();
				break;
			case ATTACK:
				attackBehavior();
				break;
			case DEFEND:
				break;
			case MICRO:
				microBehavior();
				break;
			case EXPLORE:
				exploreBehavior();
				break;
			case TAKE_ENCAMPMENT:
				takeEncampmentBehavior();
				break;
			default:
				break;
			}
			stepOffMine();
			if(rc.isActive())
				mover.execute();
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		if (target !=null)
		{
			rc.setIndicatorString(1, "Target is " + target.toString() + " " + Clock.getRoundNum());
		}
		else 
		{
			rc.setIndicatorString(1, "No Target " + Clock.getRoundNum());
		}
		p = c; //updates the past location.
	}

	protected MessageHandler getAttackHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				MapLocation new_target = new MapLocation(message[1], message[2]);
				int new_priority = message[3];

				if(target == null || PrioritySystem.rate(new_priority, Utils.naiveDistance(rc.getLocation(), new_target)) >
					PrioritySystem.rate(priority, Utils.naiveDistance(rc.getLocation(), target))) {
					target = new_target;
					priority = new_priority;
					mode = ATTACK;
				}
			}
		};
	}
	
	private void considerSwitchingModes() {
		if(rc.senseNearbyGameObjects(Robot.class, Utils.ENEMY_HQ, 1000000, Utils.ALLY_TEAM).length > 8) {
			mode = ATTACK;
		} 
		if(microSystem.enemyNearby())
		{
			mode = MICRO;
		}
		else {
			mode = IDLE;
		}
		rc.setIndicatorString(0, mode.name() + " " + Clock.getRoundNum());
		
	}

	/**
	 * Searches for empty encampments to build on.
	 * @return The best encampment found, or null otherwise.
	 * @throws GameActionException 
	 */
	private MapLocation findEmptyEncampment() throws GameActionException {
		
		MapLocation best = null;
		int distance = Integer.MAX_VALUE;
		
		for(int i = 3; i <= 6; i++) {
			MapLocation[] encampments = rc.senseEncampmentSquares(rc.getLocation(), 1 << (2*i), Team.NEUTRAL);
			for(int j = 0; j < encampments.length; j++) {
				MapLocation loc = encampments[j];
				
				if(i > 3 && rc.getLocation().distanceSquaredTo(loc) < 1 << (2*(i-1)))
					continue;
				
				if(rc.canSenseSquare(loc) && rc.senseObjectAtLocation(loc) != null)
					continue;
				
				int d = Utils.naiveDistance(rc.getLocation(), loc);
				if(d < distance) {
					best = loc;
					distance = d;
				}
			}
		}
		
		return best;
	}
	
//	protected static volatile transient int essential;
//	private static final synchronized strictfp void important() throws Exception {};
	
	private void idleBehavior() throws GameActionException {
		charging = false;
		
		//see if there is an encampment nearby to take
		
		if(target == null && rc.getTeamPower() > (1 + rc.senseAlliedEncampmentSquares().length)* 20.0) {
			MapLocation[] encampments = rc.senseEncampmentSquares(rc.getLocation(), 16, Team.NEUTRAL);
			if(encampments.length > 0) {
				int maxdist = 20;
				for(MapLocation encampment : encampments) {
					int dist = encampment.distanceSquaredTo(rc.getLocation());
					if(dist < maxdist) {
						maxdist = dist;
						target = encampment;
					}
				}
			}
		} else if (target != null) { //see if the encampment is taken
			int dist = rc.getLocation().distanceSquaredTo(target);
			if(dist > 0 && dist <= 14) {//change this to deal with upgrades!
				GameObject there = rc.senseObjectAtLocation(target);
				if(there != null) {
					target = null;
				}
			}
		}

		//rc.setIndicatorString(0, encampTarget.toString());

		if(rc.senseEncampmentSquare(rc.getLocation()) && rc.senseCaptureCost() < rc.getTeamPower()) {
			try {
				rc.captureEncampment(RobotType.SUPPLIER);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			if(rc.isActive()) {
				mineLayer.randomize();
				if (mineLayer.adjacentToEncampment()&& Math.random() < mineLayer.mineProb*5)
				{
					mineLayer.mineAroundEncampment();
				}
				else{
					if(Math.random() < mineLayer.mineProb*3) {
						rc.setIndicatorString(0, "RANDOM MINE");
						rc.layMine();
					} else {
						rc.setIndicatorString(0, mode.name());
						mover.setTarget(target == null ? gather : target);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void attackBehavior() {
		if(rc.senseNearbyGameObjects(Robot.class, gather, 20, myTeam).length > 14 || rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), 30, myTeam).length > 8) {
			charging = true;
		} else {
			charging = false;
		}
		
		if (!charging) {
			target = gather;
		} else {
			target = enemyBase;
		}
		
		//mover.aboutMoveMine(rc.getLocation().directionTo(target));
		mover.setTarget(target);
		mover.execute();
	}

	private void microBehavior() throws GameActionException {
		microSystem.run();
	}
	
	private void exploreBehavior() {
		// very dumb
		mover.setTarget(enemyBase);
		mover.execute();
	}
	
	private void takeEncampmentBehavior() {
		
	}
	
	public boolean onMine()
	{
		c = rc.getLocation();
		if (Utils.isEnemyMine(c)){
			return true;
		}
		return false;
	}
	
	/**
	 * Increments the number of stepped on mines.
	 */
	public void incrementEnemyMineCount(){
		//read number. Add one. Write new number.
	}
	
	/**
	 * 
	 */
	
	public void stepOffMine(){
		if (onMine())
		{
			mover.setTarget(p);
		}
	}
	/**
	 * Mine defusing behavior
	 */
}