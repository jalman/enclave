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
	private final int NC = 8;
	
	public MapLocation[][] encampments = new MapLocation[NC][];
	public boolean[][] taken = new boolean[NC][];
	int delimit;
	
	boolean[] finished = new boolean[NC];
	
	int suppliers = 0;
	int generators = 0;
	
	public ExpandSystem() {
		try {
			initializeDelimiters();
			findEncampments();
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		
	}
	
	public void initializeDelimiters() throws GameActionException {
		delimit = Math.max(MAP_HEIGHT, MAP_WIDTH) / (2 * NC);
	}
	
	public void findEncampments() throws GameActionException {
		for(int i = 0; i < NC; i++) {
			encampments[i] = RC.senseEncampmentSquares(ALLY_HQ, delimit * (i+1) * delimit * (i+1), null);
			taken[i] = new boolean[encampments[i].length];
		}
	}
	
	/**
	* far = 0: only nearby
	* far = 1: medium
	* far = 2: any
	 * @throws GameActionException 
	**/
	public void considerExpanding(int far) throws GameActionException {
		if(far >= NC) return;
		
		while(finished[far] || encampments[far] == null) {
			far++;
			if(far >= NC) return;
		}
		for(int i = 0; i < encampments[far].length; i++) {
			MapLocation loc = encampments[far][i];
			if(!taken[far][i] && (!RC.canSenseSquare(loc) || RC.senseObjectAtLocation(loc) == null)) {
				if(generators > suppliers) {
					messagingSystem.writeTakeEncampmentMessage(loc, 1000, RobotType.SUPPLIER);
					suppliers++;
				} else {
					messagingSystem.writeTakeEncampmentMessage(loc, 1000, RobotType.GENERATOR);
					generators++;
				}
				taken[far][i] = true;
				return;
			}
		}
		finished[far] = true;
		considerExpanding(far+1);
	}
	
}
