package team059.utils;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Utils {
	private final RobotController rc;
	
	public Utils(RobotController rc) {
		this.rc = rc;
	}
	
	public static int naiveDistance(MapLocation loc0, MapLocation loc1) {
		int dx = Math.abs(loc0.x-loc1.x);
		int dy = Math.abs(loc0.y-loc1.y);
		return Math.max(dx, dy);
	}
}
