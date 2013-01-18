package team059.utils;

import battlecode.common.Clock;
import team059.Strategy;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class Utils {
	//actual constants
	public static int[] DX = {-1, -1, -1, 0, 0, 1, 1, 1};
	public static int[] DY = {-1, 0, 1, -1, 1, -1, 0, 1};

	
	//these are set from the beginning of the game
	public static RobotController RC;
	public static int MAP_WIDTH, MAP_HEIGHT;
	private static MapLocation[][] map;
	public static Team ALLY_TEAM, ENEMY_TEAM;
	public static MapLocation ALLY_HQ, ENEMY_HQ;
	
	//these might be set at the beginning of the round
	public static Strategy strategy;
	
	public static MapLocation currentLocation;
	private static MapLocation[] alliedEncampments;
	
	
	public static void initUtils(RobotController rc) {
		RC = rc;
		
		MAP_WIDTH = rc.getMapWidth();
		MAP_HEIGHT = rc.getMapHeight();
		map = new MapLocation[MAP_WIDTH][MAP_HEIGHT];
		
		ALLY_TEAM = rc.getTeam();
		ENEMY_TEAM = (ALLY_TEAM == Team.A) ? Team.B : Team.A;
		ALLY_HQ = rc.senseHQLocation();
		ENEMY_HQ = rc.senseEnemyHQLocation();
	}
	
	/**
	 * Called at the beginning of each round.
	 */
	public static void updateUtils() {
		currentLocation = RC.getLocation();
		alliedEncampments = null;
	}
	
	public static MapLocation mapLocation(int x, int y) {
		MapLocation loc = map[x][y];
		return loc != null ? loc : (map[x][y] = new MapLocation(x, y));
	}
	
	public static boolean isEnemyMine(Team team) {
		return !(team == ALLY_TEAM || team == null);
	}

	public static boolean isEnemyMine(MapLocation loc) {
		return isEnemyMine(RC.senseMine(loc));
	}
	
	public static int naiveDistance(MapLocation loc0, MapLocation loc1) {
		return Math.max(Math.abs(loc0.x-loc1.x), Math.abs(loc0.y-loc1.y));
	}
	
	public static int naiveDistance(int x1, int y1, int x2, int y2) {
		return Math.max(Math.abs(x1-x2), Math.abs(y1-y2));
	}
	
	
	public static int mapLocationToInt(MapLocation loc) {
		return (loc.x << 16) ^ loc.y;
	}

	public static final int YMASK = (1 << 16) - 1;
	
	public static MapLocation intToMapLocation(int loc) {
		return new MapLocation(loc >> 16, loc & YMASK);
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
}
