package team059.hq;

import team059.Strategy;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.Team;
import static team059.utils.Utils.*;

/**
 * Coordinates early-game expanding.
 * @author vlad
 */
public class ExpandSystem {
	public MapLocation[][] encampments = new MapLocation[3][];
	int[] delimit = new int[2];
	
	int suppliers = 0;
	int generators = 0;
	
	public ExpandSystem() {
		initializeDelimiters();
		try {
			findEncampments();
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		
	}
	
	public void initializeDelimiters() {
		switch (strategy) {
		case RUSH:
			delimit[0] = 17;
			delimit[1] = 49;
			break;
		case NUCLEAR:
			delimit[0] = 0;
			delimit[1] = 0;
			break;
		default: //case NORMAL:
			delimit[0] = 49;
			delimit[1] = 199;
			break;
		}
		
	}
	
	public void findEncampments() throws GameActionException {
		encampments[0] = RC.senseEncampmentSquares(currentLocation, delimit[0], Team.NEUTRAL);
		encampments[1] = RC.senseEncampmentSquares(currentLocation, delimit[1], Team.NEUTRAL);
		encampments[2] = RC.senseAllEncampmentSquares();
	}
	
	/**
	* far = 0: only nearby
	* far = 1: medium
	* far = 2: any
	 * @throws GameActionException 
	**/
	public void considerExpanding(int far) throws GameActionException {
		for(MapLocation loc : encampments[far]) {
			if(RC.senseObjectAtLocation(loc) == null) {
				messagingSystem.writeTakeEncampmentMessage(loc, 30 - 5*far, generators > suppliers ? RobotType.SUPPLIER : RobotType.GENERATOR);
				return;
			}
		}
	}
	
}
