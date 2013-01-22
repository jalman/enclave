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
	
	boolean[] finished = new boolean[3];
	
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
		encampments[0] = RC.senseEncampmentSquares(ALLY_HQ, delimit[0], Team.NEUTRAL);
		encampments[1] = RC.senseEncampmentSquares(ALLY_HQ, delimit[1], Team.NEUTRAL);
		encampments[2] = RC.senseAllEncampmentSquares();
	}
	
	/**
	* far = 0: only nearby
	* far = 1: medium
	* far = 2: any
	 * @throws GameActionException 
	**/
	public void considerExpanding(int far) throws GameActionException {
		while(finished[far]) {
			far++;
		}
		for(MapLocation loc : encampments[far]) {
			if(!RC.canSenseSquare(loc) || RC.senseObjectAtLocation(loc) == null) {
				if(generators > suppliers) {
					messagingSystem.writeTakeEncampmentMessage(loc, 1000000, RobotType.SUPPLIER);
					suppliers++;
				} else {
					messagingSystem.writeTakeEncampmentMessage(loc, 1000000, RobotType.GENERATOR);
					generators++;
				}
								return;
			}
		}
		finished[far] = true;
		considerExpanding(far+1);
	}
	
}
