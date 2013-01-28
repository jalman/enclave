package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import static team059.utils.Utils.*;

public class DefuseManager extends TaskGiver {
	
	private DefuseTask defuseTask;
	
	@Override
	public void compute() throws GameActionException {
		if(defuseTask != null && defuseTask.done()) {
			defuseTask = null;
		}
		
		MapLocation mine = null;
		int distance = Integer.MAX_VALUE;
		
		MapLocation[] enemyMines = RC.senseMineLocations(currentLocation, mineRange2, ENEMY_TEAM);
		for(MapLocation loc : enemyMines) {
			if(Mines.defuse[loc.x][loc.y] < Clock.getRoundNum() - GameConstants.MINE_DEFUSE_DELAY) {
				int d = loc.distanceSquaredTo(ENEMY_HQ);
				if(d < distance) {
					mine = loc;
					d = distance;
				}
			}
		}
		
		if(mine != null) {
			defuseTask = new DefuseTask(mine, parameters.attack);
		}
	}

	@Override
	public Task getTask() {
		return defuseTask;
	}

}
