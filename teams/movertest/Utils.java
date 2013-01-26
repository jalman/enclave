package movertest;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class Utils {
	public static RobotController RC;
	public static int MAP_WIDTH, MAP_HEIGHT;
	public static Team ALLY_TEAM, ENEMY_TEAM;
	public static MapLocation ALLY_HQ, ENEMY_HQ;
	
	//public static Strategy strategy;
	
	public static int[] DX = {-1, -1, -1, 0, 0, 1, 1, 1};
	public static int[] DY = {-1, 0, 1, -1, 1, -1, 0, 1};
	
	public static void initUtils(RobotController rc) {
		RC = rc;
		
		MAP_WIDTH = rc.getMapWidth();
		MAP_HEIGHT = rc.getMapHeight();
		
		ALLY_TEAM = rc.getTeam();
		ENEMY_TEAM = (ALLY_TEAM == Team.A) ? Team.B : Team.A;
		ALLY_HQ = rc.senseHQLocation();
		ENEMY_HQ = rc.senseEnemyHQLocation();
	}
	
	
	public static boolean isEnemyMine(Team team) {
		return !(team == ALLY_TEAM || team == null);
	}

	public static boolean isEnemyMine(MapLocation loc) {
		return isEnemyMine(RC.senseMine(loc));
	}
	
	private static int dx, dy;
	
	public static int naiveDistance(MapLocation loc0, MapLocation loc1) { // call takes 33 bytecodes
		dx = loc0.x > loc1.x ? loc0.x - loc1.x : loc1.x - loc0.x;
		dy = loc0.y > loc1.y ? loc0.y - loc1.y : loc1.y - loc0.y;
		return dx > dy ? dx : dy;
	}
	
//	public static int naiveDistance(MapLocation loc0, MapLocation loc1) { // call takes 36 bytecodes
//		return Math.max(Math.abs(loc0.x-loc1.x), Math.abs(loc0.y-loc1.y));
//	}
	
	public static int naiveDistance(int x1, int y1, int x2, int y2) {
		dx = x1 > x2 ? x1-x2 : x2-x1;
		dy = y1 > y2 ? y1-y2 : y2-y1;
		return dx > dy ? dx : dy;
		//return Math.max(Math.abs(x1-x2), Math.abs(y1-y2));
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
}