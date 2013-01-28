package team059.soldiers;

import static team059.utils.Utils.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Team;

/**
 * Coordinates mine defusion.
 * @author vlad
 *
 */
public class Mines {
	public static int[][] defuse = new int[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_WIDTH];
	
	/**
	 * Defuses the farthest mine towards the target.
	 * @param target The target location.
	 * @param team The type of mine to defuse.
	 * @return Whether defusion was successful.
	 * @throws GameActionException
	 */
	public static boolean tryDefuse(MapLocation target, Team team) throws GameActionException {
		MapLocation best = null;
		int distance = currentLocation.distanceSquaredTo(target);
		
		MapLocation[] mines = RC.senseMineLocations(currentLocation, mineRange2, team);
		for(MapLocation loc : mines) {
			if(Mines.defuse[loc.x][loc.y] < Clock.getRoundNum() - GameConstants.MINE_DEFUSE_DELAY) {
				int d = loc.distanceSquaredTo(target);
				if(d < distance) {
					best = loc;
					d = distance;
				}
			}
		}
		
		if(best != null) {
			messagingSystem.writeDefusingMineMessage(best, 0);
			RC.defuseMine(best);
			return true;
		} else {
			return false;
		}
	}
	
}
