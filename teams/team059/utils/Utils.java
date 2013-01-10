package team059.utils;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class Utils {
	private RobotController rc = null;
	public Team myTeam;
	public int width, height;
	public Team enemyTeam;
	
	public int[][] adj_tile_offsets = {
		{ -1, -1 },
		{ -1,  0 },
		{ -1,  1 }, 
		{  0, -1 },
		{  0,  1 },
		{  1, -1 },
		{  1,  0 },
		{  1,  1 }
	};
	
	public Utils(RobotController rc) {
		this.rc = rc;
		height = rc.getMapHeight();
		width = rc.getMapWidth();
		this.myTeam = rc.getTeam();
		this.enemyTeam = ( myTeam == Team.A ? Team.B : Team.A );
	}
	
	@Deprecated
	public Team enemy() {
		return rc.getTeam() == Team.A ? Team.B : Team.A;
	}
	
	public int naiveDistance(MapLocation m1, MapLocation m2) {
		return Math.max(Math.abs(m1.x-m2.x), Math.abs(m1.y-m2.y));
	}
	
	public boolean isEnemyMine(Team team) {
		return !(team == myTeam() || team == null);
	}

	public boolean isEnemyMine(MapLocation loc) {
		return isEnemyMine(rc.senseMine(loc));
	}
	
	public int locToInt(MapLocation loc) { 
		return height*loc.y + loc.x;
	}
	
	public MapLocation intToLoc(int encoded_loc) {
		return new MapLocation(encoded_loc % height, encoded_loc / height);
	}
	
	public Team myTeam() {
		if(myTeam != null) {
			return myTeam;
		}
		myTeam = rc.getTeam();
		return myTeam;
	}
	
	public void broadcast(MessageType type, int data) {
		try {
			rc.broadcast(type.hashCode() % 10000, data);			//change from hashCode to something reasonable
		} catch (GameActionException e) {
		}	
	}
	
	public int readBroadcast(MessageType type) {
		try {
			return rc.readBroadcast(type.hashCode() % 10000);		//change from hashCode to something reasonable
		} catch (GameActionException e) {
			return -1;
		}
	}
	
}
