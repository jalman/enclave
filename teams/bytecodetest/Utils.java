package bytecodetest;

import java.util.Arrays;
import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class Utils {
	
	//Game constants
	public final static int MAX_SOLDIER_ENERGON = 40;
	public final static int MAX_ENCAMPMENT_ENERGON = 100;
	public final static int MAX_HQ_ENERGON = 500;
	public static final RobotType[] ROBOT_TYPE = RobotType.values();
	
	//actual constants
	public static int[] DX = {-1, -1, -1, 0, 0, 1, 1, 1};
	public static int[] DY = {-1, 0, 1, -1, 1, -1, 0, 1};
	public static Direction[] DIRECTIONS = Direction.values();
	
	// Mining constants
	public static final int CHECK_MINE_RADIUS_SQUARED = 13; // make sure this matches CHECK_MINE_RADIUS!!!
	public static final int CHECK_MINE_RADIUS = 4;

	
	
	//these are set from the beginning of the game
	public static RobotController RC;
	public static int MAP_WIDTH, MAP_HEIGHT;
	public static Team ALLY_TEAM, ENEMY_TEAM;
	public static MapLocation ALLY_HQ, ENEMY_HQ;
	public static Random random;
	public static int birthRound;
	
	//this is for messaging
//	public static MessagingSystem messagingSystem;
	public static int ID;
	
	//these might be set at the beginning of the round
//	public static Strategy strategy = Strategy.NORMAL;
	
	public static MapLocation currentLocation;
	public static int curX, curY;
	private static MapLocation[] alliedEncampments;
	public static final int ENEMY_RADIUS = 4;
	public static final int ENEMY_RADIUS2 = ENEMY_RADIUS * ENEMY_RADIUS;
	public static Robot[] enemyRobots = new Robot[0];
	public static double forward;
	
	public static boolean[] UPGRADES_RESEARCHED = new boolean[Upgrade.values().length];
	
	public static final int[] SQUARES_IN_RANGE = 
		{1, 5, 9, 9, 13, 21, 21, 21, 25, 29, 
		37, 37, 37, 45, 45, 45, 49, 57, 61, 61, 
		69, 69, 69, 69, 69, 81, 89, 89, 89, 97, 
		97, 97, 101, 101, 109, 109, 113, 121, 121, 121, 
		129, 137, 137, 137, 137, 145, 145, 145, 145, 149, 
		161, 161, 169, 177, 177, 177, 177, 177, 185, 185, 
		185, 193, 193, 193, 197, 213, 213, 213, 221, 221, 
		221, 221, 225, 233, 241, 241, 241, 241, 241, 241, 
		249, 253, 261, 261, 261, 277, 277, 277, 277, 285, 
		293, 293, 293, 293, 293, 293, 293, 301, 305, 305, 
		317};
	
	public static void initUtils(RobotController rc) {
		RC = rc;
		ID = RC.getRobot().getID();
		
		MAP_WIDTH = rc.getMapWidth();
		MAP_HEIGHT = rc.getMapHeight();
		
		ALLY_TEAM = rc.getTeam();
		ENEMY_TEAM = (ALLY_TEAM == Team.A) ? Team.B : Team.A;
		ALLY_HQ = rc.senseHQLocation();
		ENEMY_HQ = rc.senseEnemyHQLocation();
		
		birthRound = Clock.getRoundNum();
		
		random = new Random(((long)ID<< 32) ^ Clock.getRoundNum());

//		messagingSystem = new MessagingSystem();
		
		for(Upgrade upgrade : Upgrade.values()) {
			UPGRADES_RESEARCHED[upgrade.ordinal()] = RC.hasUpgrade(upgrade);
		}
		updateUtils();
	}
	
	/**
	 * Called at the beginning of each round.
	 */
	public static void updateUtils() {
		currentLocation = RC.getLocation();
		curX = currentLocation.x;
		curY = currentLocation.y;
		alliedEncampments = null;
		forward = Math.log((double)naiveDistance(currentLocation, ALLY_HQ) / naiveDistance(currentLocation, ENEMY_HQ));
		enemyRobots = RC.senseNearbyGameObjects(Robot.class, currentLocation, ENEMY_RADIUS2, ENEMY_TEAM);
	}
	
	public static boolean isEnemyMine(Team team) {
		return !(team == ALLY_TEAM || team == null);
	}

	public static boolean isEnemyMine(MapLocation loc) {
		return isEnemyMine(RC.senseMine(loc));
	}
	
	private static int dx, dy;
	
	public static int naiveDistance(MapLocation loc0, MapLocation loc1) { 
		// call takes 33 bytecodes
//		dx = loc0.x > loc1.x ? loc0.x - loc1.x : loc1.x - loc0.x;
//		dy = loc0.y > loc1.y ? loc0.y - loc1.y : loc1.y - loc0.y;
//		int c = dx > dy ? dx : dy;
//		int bc = Clock.getBytecodeNum();
		dx = loc0.x - loc1.x; // call takes 31 bytecodes
		dy = loc0.y - loc1.y;
		dx = dx*dx > dy*dy ? dx : dy;
		return dx > 0? dx : -dx;
//		int c = dx > 0 ? dx : -dx;
//		int c = Math.max(Math.max(dx, dy), Math.max(-dx, -dy));
		//return naiveDistance(loc0.x, loc0.y, loc1.x, loc1.y);
//		System.out.println("bc used by naiveDistance: " + (Clock.getBytecodeNum()-bc));
//		return c;
	}
	
//	public static int naiveDistance(MapLocation loc0, MapLocation loc1) { // call takes 36 bytecodes
//		return Math.max(Math.abs(loc0.x-loc1.x), Math.abs(loc0.y-loc1.y));
//	}
	
	public static int naiveDistance(int x1, int y1, int x2, int y2) {
		dx = x1 > x2 ? x1-x2 : x2-x1;
		dy = y1 > y2 ? y1-y2 : y2-y1;
		return dx > dy ? dx : dy;
//		dx = x1 - x2;
//		dy = y1 - y2;
//		dx = dx*dx > dy*dy ? dx : dy;
//		return dx > 0 ? dx : -dx;
		//return Math.max(Math.abs(x1-x2), Math.abs(y1-y2));
	}
	
	public static boolean isFirstRound() {
		return Clock.getRoundNum() == birthRound;
	}
	
	/**
	 * Finds the closest (by naive distance) map location to the target among a set of map locations.
	 * @param locs The set of map locations.
	 * @param target The target location.
	 * @return The closest map location.
	 */
	public static MapLocation closest(MapLocation[] locs, MapLocation target) {
		MapLocation close = null;
		int distance = Integer.MAX_VALUE;
		
		for(int i = 0; i < locs.length; i++) {
			int d = naiveDistance(locs[i], target);
			if(d < distance) {
				close = locs[i];
				distance = d;
			}
		}
		
		return close;
	}
	
	public static MapLocation[] getAlliedEncampments() {
		if(alliedEncampments == null) {
			alliedEncampments = RC.senseAlliedEncampmentSquares();
		}
		return alliedEncampments;
	}
	
	public static int clamp(int i, int min, int max) {
		if(i < min) return min;
		if(i > max) return max;
		return i;
	}
	
	public static <T> T[] newArray(int length, T... array) {
		return Arrays.copyOf(array, length);
	}
	
	
}
