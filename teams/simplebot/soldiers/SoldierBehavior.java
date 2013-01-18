package simplebot.soldiers;

import java.util.Random;

import simplebot.RobotBehavior;
import simplebot.messaging.MessageHandler;
import simplebot.movement.Mover;
import simplebot.movement.NavType;
import simplebot.soldiers.mineLay.MineLayer;
import static simplebot.utils.Utils.*;

import battlecode.common.*;
import static simplebot.soldiers.SoldierMode.*;

public class SoldierBehavior extends RobotBehavior {

	private SoldierMode mode;
	public MapLocation target = null, waypoint1 = null, waypoint2 = null, messageTarget;
//	private int priority;
	private MapLocation[] gather;
	private MapLocation myGather;
	private boolean charging = false, messageWritten = false;
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
	
	public SoldierBehavior() throws GameActionException {
		curLoc = RC.getLocation();
		previousLocation = RC.getLocation();
		mover = new Mover((RobotBehavior) this);
		mineLayer = new MineLayer(RC);
		mode = SoldierMode.IDLE; // for now
		rand = new Random(Clock.getRoundNum() * RC.getRobot().getID() + Clock.getBytecodeNum());
		
		
		//set gather points and assignment
		gather = new MapLocation[3]; 
		
		gather[0] = new MapLocation((ALLY_HQ.x * 4 + ENEMY_HQ.x ) / 5, (ALLY_HQ.y * 4 + ENEMY_HQ.y ) / 5);
		MapLocation third = new MapLocation((ALLY_HQ.x * 2 + ENEMY_HQ.x ) / 3, (ALLY_HQ.y * 2 + ENEMY_HQ.y ) / 3);
		MapLocation twothird = new MapLocation((ALLY_HQ.x + ENEMY_HQ.x * 2) / 3, (ALLY_HQ.y + ENEMY_HQ.y * 2) / 3);
		int thirdy = (ALLY_HQ.y - ENEMY_HQ.y)/3;
		int thirdx = (- ALLY_HQ.x + ENEMY_HQ.x)/3;
		gather[1] = third.add(thirdy, thirdx);
		gather[2] = third.add(-thirdy, -thirdx);
		
		double r = rand.nextDouble();
		myAssignment = 0;
		if (r > 0.2) {
			myAssignment = 0;
		} else if (r > 0.1) {
			myAssignment = 1;
		} else {
			myAssignment = 2;
		}
		
		myGather = gather[myAssignment];
		
		if(myAssignment == 0) {  //later: reorder these things to be reasonable
			sequence = new MapLocation[4];
			sequence[0] = myGather;
			sequence[1] = myGather;
			sequence[2] = new MapLocation((ENEMY_HQ.x+myGather.x)/2, (ENEMY_HQ.y+myGather.y)/2);
			sequence[3] = ENEMY_HQ;
		} else if (myAssignment == 1) {
			sequence = new MapLocation[4];
			sequence[0] = myGather;
			sequence[1] = twothird.add((int)(1.4 * thirdy), (int)(1.4 * thirdx));
			sequence[2] = ENEMY_HQ.add((int)(1.4 * thirdy), (int)(1.4 * thirdx));
			sequence[3] = ENEMY_HQ;
		} else if (myAssignment == 2) {
			sequence = new MapLocation[4];
			sequence[0] = myGather;
			sequence[1] = twothird.add((int)(-1.4 * thirdy), (int)(-1.4 * thirdx));
			sequence[2] = ENEMY_HQ.add((int)(-1.4 * thirdy), (int)(-1.4 * thirdx));
			sequence[3] = ENEMY_HQ;
		}
	}

	@Override
	public void run() {
		if(!RC.isActive()) return;
		
		messagingSystem.handleMessages(messageHandlers);
		enemyInVicinity = false;
		curLoc = RC.getLocation();
		if(RC.isActive())
			mover.execute();
		
		//Possible issue; microSystem uses its own targets. Could this be bad?
		if (mover.getTarget() !=null)
		{
			try {
				RC.setIndicatorString(1, "Target is " + mover.getTarget().toString() + " " + Clock.getRoundNum());
			} catch(NullPointerException e) {
				System.out.println(mover.getTarget());
				throw e;
			}
		}
		else 
		{
			RC.setIndicatorString(1, "No Target " + Clock.getRoundNum());
		}
		previousLocation = curLoc; //updates the past location.
	}

	
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
}
