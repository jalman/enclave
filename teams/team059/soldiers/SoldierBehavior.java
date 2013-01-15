package team059.soldiers;

import java.util.Random;

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
	private MapLocation target = null, waypoint1 = null, waypoint2 = null, messageTarget;
//	private int priority;
	private MapLocation[] gather;
	private MapLocation myGather;
	private boolean charging = false, messageWritten = false;
	private Micro microSystem;
	private MapLocation curLoc = null, previousLocation = null;
	private boolean enemyInVicinity = false; //messaging system tells if there's an enemy within battleDistance away.
	
	Random rand;
	
	GameObject[] enemies = new GameObject[0], allies = new GameObject[0];
	RobotInfo[] enemySoldiers = new RobotInfo[0], alliedSoldiers = new RobotInfo[0];
	
	int goingToBattle = 0;
	public final Mover mover;
	public final MineLayer mineLayer;
	
	public SoldierBehavior(RobotController therc) throws GameActionException {
		super(therc);
		curLoc = rc.getLocation();
		previousLocation = rc.getLocation();
		mover = new Mover((RobotBehavior) this);
		mineLayer = new MineLayer(rc);
		mode = SoldierMode.IDLE; // for now
		microSystem = new Micro(this);
		rand = new Random(Clock.getRoundNum() * rc.getRobot().getID() + Clock.getBytecodeNum());
		gather = new MapLocation[3]; 
		
		gather[0] = new MapLocation((myBase.x * 2 + enemyBase.x ) / 3, (myBase.y * 2 + enemyBase.y ) / 3);
		gather[1] = gather[0].add((myBase.y - enemyBase.y)/3, (- myBase.x + enemyBase.x)/3);
		gather[2] = gather[0].add((- myBase.y + enemyBase.y)/3, (myBase.x - enemyBase.x)/3);
		
		double r = rand.nextDouble();
		if (r > 0.5) {
			myGather = gather[0];
		} else if (r > 0.25) {
			myGather = gather[1];
		} else {
			myGather = gather[2];
		}
	}

	@Override
	public void run() {
		if(!rc.isActive()) return;
		
		messagingSystem.handleMessages(messageHandlers);
		enemyInVicinity = false;
		curLoc = rc.getLocation();
		try {
			considerSwitchingModes();
			
			switch(mode) {
			case IDLE:
				idleBehavior();
				break;
			case ATTACK:
				attackBehavior();
				break;
			case DEFEND:
				break;
			case CHARGING_TO_BATTLE:
				battleBehavior();
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
			if (mode != CHARGING_TO_BATTLE)
			{
				enemyInVicinity = false;
			}
			stepOffMine();
			if(rc.isActive())
				mover.execute();
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		
		//Possible issue; microSystem uses its own targets. Could this be bad?
		if (mover.getTarget() !=null)
		{
			rc.setIndicatorString(1, "Target is " + mover.getTarget().toString() + " " + Clock.getRoundNum());
		}
		else 
		{
			rc.setIndicatorString(1, "No Target " + Clock.getRoundNum());
		}
		previousLocation = curLoc; //updates the past location.
	}

	@Override
	protected MessageHandler getAttackHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				MapLocation new_target = new MapLocation(message[1], message[2]);
				int new_priority = message[3];
				if (PrioritySystem.rate(Utils.naiveDistance(curLoc, new_target)) == 1)
				{
					messageTarget = new_target;
					target = new_target;
					enemyInVicinity = true;
				}
				
				/*if(target == null || PrioritySystem.rate(new_priority, Utils.naiveDistance(rc.getLocation(), new_target)) >
					PrioritySystem.rate(priority, Utils.naiveDistance(rc.getLocation(), target))) {
					target = new_target;
					priority = new_priority;
					mode = ATTACK;
				}*/
			}
		};
	}

	/**
	 * Checkpoint number is at either 0, 1, 2, or 3
	 * 0 = HQ
	 * 1 = TBD
	 * 2 = TBD
	 * 3 = Enemy HQ 
	 */
	
	@Override
	protected MessageHandler getCheckpointHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				int pointNumber = message[1];
			}
		};
	}
	
	private void considerSwitchingModes() throws GameActionException {
		
		if(microSystem.enemySoldierNearby(Micro.sensorRadius)){
			mode = MICRO;
		}
		else if (enemyInVicinity){
			
			mover.setTarget(messageTarget);
			if (mode != CHARGING_TO_BATTLE)
			{
				mode = CHARGING_TO_BATTLE;
				goingToBattle = 0;
			}
			goingToBattle ++;
			
			if(goingToBattle >= 10)
			{
				mode = IDLE;
				goingToBattle = 0;
			}
		}
		else if(rc.senseNearbyGameObjects(Robot.class, Utils.ENEMY_HQ, 1000000, Utils.ALLY_TEAM).length > 8) {
			mode = ATTACK;
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
		mover.toggleDefuseMoving(true);
		
		//see if there is an encampment nearby to take
		
		if(target == null && rc.getTeamPower() > (1 + rc.senseAlliedEncampmentSquares().length)* 20.0) {
			MapLocation[] encampments = rc.senseEncampmentSquares(rc.getLocation(), 100, Team.NEUTRAL);
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
				if (mineLayer.adjacentToEncampment()&& Math.random() < mineLayer.mineProb*3)
				{
					rc.setIndicatorString(0, "ENCAMPMENT MINE");
					mineLayer.mineAroundEncampment();
				}
				else{
					if(Math.random() < mineLayer.mineProb*3/4) {
						rc.setIndicatorString(0, "RANDOM MINE");
						rc.layMine();
					} else {
						rc.setIndicatorString(0, mode.name());
						mover.setTarget(target == null ? myGather : target);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void attackBehavior() {
		mover.toggleDefuseMoving(true);
		if(rc.senseNearbyGameObjects(Robot.class, gather[0], 20, myTeam).length > 13 || rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), 30, myTeam).length > 10) {
			charging = true;
		}
		target = enemyBase;
		
		//mover.aboutMoveMine(rc.getLocation().directionTo(target));
		mover.setTarget(target);
		mover.execute();
	}

	private void battleBehavior() throws GameActionException {
		mover.toggleDefuseMoving(false);
		attackTarget(messageTarget);
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
		if (Utils.isEnemyMine(curLoc)){
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
	
	public void attackTarget(MapLocation m) throws GameActionException
	{
		if (curLoc.distanceSquaredTo(m) > 2)
		{
			target = m;
			mover.setTarget(target);
		}
	}
	
	public void stepOffMine(){
		if (onMine())
		{
			if(mover.getTarget() == curLoc)
			{
				mover.setTarget(previousLocation);
			}
		}
	}
	/**
	 * Mine defusing behavior
	 */
}