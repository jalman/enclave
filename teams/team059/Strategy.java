package team059;

import static team059.utils.Utils.*;
import battlecode.common.MapLocation;
import battlecode.common.Team;

public enum Strategy {
	NORMAL, NUKE, RUSH;
	
	public static Strategy decide() {
		int distance = naiveDistance(ALLY_HQ, ENEMY_HQ);
		
		
		MapLocation halfway = new MapLocation((ALLY_HQ.x + ENEMY_HQ.x)/2, (ALLY_HQ.y + ENEMY_HQ.y)/2);
		int dx = halfway.x - ALLY_HQ.x;
		int dy = halfway.y - ALLY_HQ.y;
		
		int mines = RC.senseMineLocations(halfway, dx*dx + dy*dy, Team.NEUTRAL).length;
		
		
		
		return NORMAL;
	}
}
