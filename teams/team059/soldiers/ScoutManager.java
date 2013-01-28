package team059.soldiers;

import team059.Strategy;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Team;
import static team059.utils.Utils.*;

public class ScoutManager extends TaskGiver {
	
	private ScoutTask scoutTask = null;
	
	/**
	 * Computes a good encampment to take.
	 * @throws GameActionException 
	 */
	@Override
	public void compute() throws GameActionException {
		if(Clock.getRoundNum() < 40 && scoutTask == null) {
			scoutTask = new ScoutTask();
		}
	}

	
	
	@Override
	public Task getTask() {
		return scoutTask;
	}

}
