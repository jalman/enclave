package team059.utils;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class Utils {
	public static RobotController RC;
	public static int MAP_WIDTH, MAP_HEIGHT;
	public static Team ALLY_TEAM, ENEMY_TEAM;
	public static MapLocation ALLY_HQ, ENEMY_HQ;
	

	public static int[][] OFFSETS = {
		{ -1, -1 },
		{ -1,  0 },
		{ -1,  1 }, 
		{  0, -1 },
		{  0,  1 },
		{  1, -1 },
		{  1,  0 },
		{  1,  1 }
	};
	
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
	
	public static int naiveDistance(MapLocation loc0, MapLocation loc1) {
		int dx = Math.abs(loc0.x-loc1.x);
		int dy = Math.abs(loc0.y-loc1.y);
		return Math.max(dx, dy);
	}
	public static MapLocation closest(MapLocation[] a, MapLocation c) //finds closest element in a to c.
	{
		MapLocation m = new MapLocation(1000,1000);
		int d = 10000;
		MapLocation loc;
		
		if (a!=null && a.length !=0)
		{
			for(int i =0; i < a.length; i++)
			{
					loc = a[i];
					if (Utils.naiveDistance(loc, c) < d)
					{
						m = loc;
						d = Utils.naiveDistance(loc, c);
					}
				}
			return m;
		}
		else
			return null;
	}
}
