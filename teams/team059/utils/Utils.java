package team059.utils;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class Utils {
	RobotController rc = null;
	Team myTeam = null;
	
	public Utils(RobotController rc) {
		this.rc = rc;
	}
	
	public Team enemy() {
		return rc.getTeam() == Team.A ? Team.B : Team.A;
	}
	
	public boolean isEnemyMine(Team team) {
		return !(team == myTeam() || team == null);
	}

	public boolean isEnemyMine(MapLocation loc) {
		return isEnemyMine(rc.senseMine(loc));
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
