package team059.hq;

import team059.Strategy;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.Team;
import static team059.utils.Utils.*;

/**
 * Coordinates early-game expanding.
 * @author timothy zaran "tiberius" chu 
 */
public class ExpandSystem {
	private final int NC = 8;
	
	private int numSent = 0;
	
	public MapLocation[][] encampments = new MapLocation[NC][];
	public boolean[][] taken = new boolean[NC][];
	int delimit;
	
	boolean[] finished = new boolean[NC];
	
	int suppliers = 0;
	int generators = 0;
	
	private int numLost = 0;
	
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
	
	public void considerExpanding() throws GameActionException {
		considerExpanding(0);
	}
	

	public void considerExpanding(int far) throws GameActionException {
		if(far >= NC) return;
		if(numSent*10 > Clock.getRoundNum()) return;
		if(numSent >= parameters.greed) return;
		///CHANGE THE 4 INTO SOMETHING ELSE?!
		if(RC.senseAlliedEncampmentSquares().length*3 + numSent > RC.senseNearbyGameObjects(Robot.class, Integer.MAX_VALUE, ALLY_TEAM).length) return;
		//System.out.println(numSent + " " + parameters.greed);
		
		while((finished[far] && numLost == 0) || encampments[far] == null) {
			far++;
			if(far >= NC) return;
		}
		for(int i = 0; i < encampments[far].length; i++) {
			MapLocation loc = encampments[far][i];
			if((!taken[far][i] || numLost > 0) && (!RC.canSenseSquare(loc) || RC.senseObjectAtLocation(loc) == null)) {
				if(suppliers < 7 || generators > suppliers) {
					messagingSystem.writeTakeEncampmentMessage(loc, 200, RobotType.SUPPLIER);
					suppliers++;
				} else {
					messagingSystem.writeTakeEncampmentMessage(loc, 200, RobotType.GENERATOR);
					generators++;
				}
				if(taken[far][i]) {
					numLost--;
				} else {
					taken[far][i] = true;
				}
				numSent++;
				return;
			}
		}
		finished[far] = true;
		considerExpanding(far+1);
	}
	
	public void lost() {
		numLost++;
	}
	
}
