package team059.soldiers;

import team059.Strategy;
import team059.movement.Mover;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import static team059.utils.Utils.*;

public class ScoutTask extends TravelTask {

	public static Mover mover = new Mover();
	public static final int SCOUT_PRIORITY = 10000;
	public boolean scoutRight; // false: Left, true: Right
	protected MapLocation[] waypoint;
	protected int waypointIndex;
	protected int MAX_SCOUT_TURNS;
	
	private MapLocation firstMine = null;
	private MapLocation firstSuppGen = null;

	
	private boolean runningAway = false;
	
	public ScoutTask() {
		super(mover, ENEMY_HQ, SCOUT_PRIORITY, 1);
		this.scoutRight = Clock.getRoundNum() < 5;
		waypoint = new MapLocation[3];
		waypointIndex = 0;

		//MapLocation third = new MapLocation((ALLY_HQ.x * 2 + ENEMY_HQ.x ) / 3, (ALLY_HQ.y * 2 + ENEMY_HQ.y ) / 3);
		//MapLocation twothird = new MapLocation((ALLY_HQ.x + ENEMY_HQ.x * 2) / 3, (ALLY_HQ.y + ENEMY_HQ.y * 2) / 3);
		int thirdy = scoutRight ? (ALLY_HQ.y - ENEMY_HQ.y)/3 : -(ALLY_HQ.y - ENEMY_HQ.y)/3;
		int thirdx = scoutRight ? (-ALLY_HQ.x + ENEMY_HQ.x)/3 : -(-ALLY_HQ.x + ENEMY_HQ.x)/3;

		waypoint[0] = new MapLocation(
				clamp((ALLY_HQ.x + ENEMY_HQ.x * 2) / 3 + (int) 1.4*thirdx, 0, MAP_WIDTH), 
				clamp((ALLY_HQ.y + ENEMY_HQ.y * 2) / 3 + (int) 1.4*thirdy, 0, MAP_HEIGHT)
		);

		waypoint[1] = new MapLocation(
				clamp(ENEMY_HQ.x + (int) 1.4*thirdx, 0, MAP_WIDTH), 
				clamp(ENEMY_HQ.y + (int) 1.4*thirdy, 0, MAP_HEIGHT)
		);

		waypoint[2] = ENEMY_HQ;
		MAX_SCOUT_TURNS = 4*naiveDistance(ALLY_HQ, ENEMY_HQ);
	}

//	@Override
//	public boolean done() {
//		super.done();
//	}

	@Override
	public int appeal() {
		//return Clock.getRoundNum() < MAX_SCOUT_TURNS ? SCOUT_PRIORITY : 0;
		if(Clock.getRoundNum() > 3 * HQ_DIST /* not a magic number */ || strategy.equals(Strategy.NUCLEAR)) {
			//System.out.println("NUCLEAR YES SCOUT");
			return - Clock.getRoundNum();
		}
		return SCOUT_PRIORITY;
	}

	@Override
	public void execute() throws GameActionException {
//		//Figure out what they are doing
//		if(RC.senseMine(currentLocation) == ENEMY_TEAM) {
//			if(firstMine == null) {
//				firstMine = currentLocation;
//			} else {
//				messagingSystem.writeHQMessage(Strategy.RUSH);
//			}
//		}
//		
//		Robot[] nearby = RC.senseNearbyGameObjects(Robot.class, 14, ENEMY_TEAM);
//		
//		
//		
//		//Move in!
		if(!runningAway) {
			if(waypointIndex < 2 && currentLocation.isAdjacentTo(waypoint[waypointIndex])) {
				waypointIndex++;
			}
			mover.setTarget(waypoint[waypointIndex]);
		} else {
			
		}
		mover.execute();
		
		int nEnemies = RC.senseNearbyGameObjects(Robot.class, 14, ENEMY_TEAM).length;
		if(nEnemies > 0) {
			seeEnemyWarn(nEnemies);
		}
	}
	
	protected void seeEnemyWarn(int n) {
		return;
	}
	
	@Override
	public String toString() {
		return "SCOUTING TO " + super.mover.getTarget();
	}
}
