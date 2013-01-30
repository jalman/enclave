package team059.soldiers;

import static team059.utils.Utils.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameActionExceptionType;
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
	 * @param enemy Whether to defuse only enemy mines.
	 * @return Whether defusion was successful.
	 * @throws GameActionException
	 */
	public static boolean tryDefuse(MapLocation target, boolean enemy) throws GameActionException {
		MapLocation best = null;
		int distance = currentLocation.distanceSquaredTo(target);
		
		
		MapLocation[] mines = enemy ?
				RC.senseMineLocations(currentLocation, mineRange2, ENEMY_TEAM) :
					RC.senseNonAlliedMineLocations(currentLocation, mineRange2);
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

//			if(enemy == false) {
//				throw new GameActionException(GameActionExceptionType.CANT_DO_THAT_BRO, "enemy = false");
//				//System.out.println("enemy = false!");
//			}
			
			return true;
		} else {
			return false;
		}
	}
	
}
