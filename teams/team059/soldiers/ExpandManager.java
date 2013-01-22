package team059.soldiers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Team;
import static team059.utils.Utils.*;

public class ExpandManager extends TaskGiver {
	
	private ExpandTask expandTask;
	
	/**
	 * Computes a good encampment to take.
	 * @throws GameActionException 
	 */
	@Override
	public void compute() throws GameActionException {
		final int initialRadius = 5;
		final int initialRadius2 = initialRadius * initialRadius;
		
		MapLocation closest = null;
		int min_distance = Integer.MAX_VALUE;
		
		for(MapLocation square : RC.senseEncampmentSquares(currentLocation, initialRadius2, Team.NEUTRAL)) {
			if(RC.canSenseSquare(square) && RC.senseObjectAtLocation(square) != null) {
				continue;
			}
			int d = naiveDistance(currentLocation, square);
			if(d < min_distance) {
				closest = square;
				min_distance = d;
			}
		}
		
		if(closest != null) {
			expandTask = new ExpandTask(closest);
		} else {
			expandTask = null;
		}
	}

	
	
	@Override
	public Task getTask() {
		return expandTask;
	}

}
