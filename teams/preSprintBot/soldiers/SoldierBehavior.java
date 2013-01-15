package preSprintBot.soldiers;

import java.util.Random;

import preSprintBot.RobotBehavior;
import preSprintBot.messaging.MessageHandler;
import preSprintBot.movement.Mover;
import preSprintBot.movement.NavType;
import preSprintBot.soldiers.micro.Micro;
import preSprintBot.soldiers.mineLay.MineLayer;
import preSprintBot.utils.Utils;

import battlecode.common.*;
import static preSprintBot.soldiers.SoldierMode.*;

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
	
	
	int myAssignment; //mid, left, right
	MapLocation[] sequence;
	int attackSequenceState = 0;
	int timeSinceSwitch = 0;
	
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
		
		
		//set gather points and assignment
		gather = new MapLocation[3]; 
		
		gather[0] = new MapLocation((myBase.x * 4 + enemyBase.x ) / 5, (myBase.y * 4 + enemyBase.y ) / 5);
		MapLocation third = new MapLocation((myBase.x * 2 + enemyBase.x ) / 3, (myBase.y * 2 + enemyBase.y ) / 3);
		MapLocation twothird = new MapLocation((myBase.x + enemyBase.x * 2) / 3, (myBase.y + enemyBase.y * 2) / 3);
		int thirdy = (myBase.y - enemyBase.y)/3;
		int thirdx = (- myBase.x + enemyBase.x)/3;
		gather[1] = third.add(thirdy, thirdx);
		gather[2] = third.add(-thirdy, -thirdx);
		
		double r = rand.nextDouble();
		if (r > 0.6) {
			myAssignment = 0;
		} else if (r > 0.3) {
			myAssignment = 1;
		} else {
			myAssignment = 2;
		}
		
		myGather = gather[myAssignment];
		
		if(myAssignment == 0) {  //later: reorder these things to be reasonable
			sequence = new MapLocation[4];
			sequence[0] = myGather;
			sequence[1] = myGather;
			sequence[2] = myGather;
			sequence[3] = myGather;
		} else if (myAssignment == 1) {
			sequence = new MapLocation[4];
			sequence[0] = myGather;
			sequence[1] = twothird.add((int)(1.4 * thirdy), (int)(1.4 * thirdx));
			sequence[2] = enemyBase.add((int)(1.4 * thirdy), (int)(1.4 * thirdx));
			sequence[3] = enemyBase;
		} else if (myAssignment == 2) {
			sequence = new MapLocation[4];
			sequence[0] = myGather;
			sequence[1] = twothird.add((int)(-1.4 * thirdy), (int)(-1.4 * thirdx));
			sequence[2] = enemyBase.add((int)(-1.4 * thirdy), (int)(-1.4 * thirdx));
			sequence[3] = enemyBase;
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
			rc.setIndicatorString(0, mode.name());
			
			switch(mode) {
			case IDLE:
				idleBehavior();
				break;
			case ATTACK:
				attackBehavior();
				break;
			case EMERGENCY_ATTACK:
				emergencyAttackBehavior();
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
			
			resetEnemyInVicinityStatus(); // rests choice constants each turn.
			
			stepOffMine();
			if(rc.isActive())
				mover.execute();
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		
		//Possible issue; microSystem uses its own targets. Could this be bad?
		if (mover.getTarget() !=null)
		{
			try {
				rc.setIndicatorString(1, "Target is " + mover.getTarget().toString() + " " + Clock.getRoundNum());
			} catch(NullPointerException e) {
				System.out.println(mover.getTarget());
				throw e;
			}
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
				messageTarget = new MapLocation(message[1], message[2]);
				int new_priority = message[3];
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
		else if (messageTarget != null && PrioritySystem.rate(Utils.naiveDistance(curLoc, messageTarget)) == 1)
		{
			setChargeMode(); // sets the robot into charge mode, and breaks out of charge mode if charging for too long	
		}
		else if(rc.senseNearbyGameObjects(Robot.class, Utils.ENEMY_HQ, 1000000, Utils.ALLY_TEAM).length > 20) {
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
	
	private void idleBehavior() throws GameActionException {
		charging = false;
		mover.setNavType(NavType.BUG_DIG_1);
		
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
				if(rc.senseCaptureCost() <= 40) {
					rc.captureEncampment(RobotType.ARTILLERY);
				} else {
					rc.captureEncampment(RobotType.SUPPLIER);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		mover.setTarget(target == null ? myGather : target);

		/*
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
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}

	private void attackBehavior() {
		mover.setNavType(NavType.BUG_DIG_3);
		charging = attackSequenceState >= 3;
		
		if(attackSequenceState < 3 && (rc.senseNearbyGameObjects(Robot.class, sequence[attackSequenceState], 20, myTeam).length >= 6 || timeSinceSwitch >= 40)) {
			attackSequenceState++;
			timeSinceSwitch = 0;
		} else {
			timeSinceSwitch ++;
		}
		
		target = sequence[attackSequenceState];
		
		mover.setTarget(target);
		mover.execute();
		
/*		mover.toggleDefuseMoving(true);
		if(rc.senseNearbyGameObjects(Robot.class, gather[0], 20, myTeam).length > 13 || rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), 30, myTeam).length > 10) {
			charging = true;
		}
		target = enemyBase;
		
		//mover.aboutMoveMine(rc.getLocation().directionTo(target));
		mover.setTarget(target);
		mover.execute();*/
	}
	
	private void emergencyAttackBehavior() {
		mover.setNavType(NavType.STRAIGHT_DIG);
		target = Utils.ENEMY_HQ;
		mover.setTarget(target);
		mover.execute();
	}


	private void battleBehavior() throws GameActionException {
		mover.setNavType(NavType.BUG);
		target = messageTarget;
		attackTarget(target);
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
	 * Increments the number of stepped on mines. Used to determine what to research
	 */
	public void incrementEnemyMineCount(){
		//read number. Add one. Write new number.
	}
	
	/**
	 * Attack target (doesn't move if next to target)
	 */
	
	public void attackTarget(MapLocation m) throws GameActionException
	{
		if (curLoc.distanceSquaredTo(m) > 2)
		{
			target = m;
			mover.setTarget(target);
		}
	}

	/**
	 * Tells soldier to step off of mine
	 */
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
	 * Sets charge mode, and enforces it to expire after 10 turns.
	 */
	public void setChargeMode(){
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

	//resets the enemyInVicinity constant to determine whether to enter charging mode
	public void resetEnemyInVicinityStatus(){
		if (mode != CHARGING_TO_BATTLE)
		{
			enemyInVicinity = false;
		}
	}
}