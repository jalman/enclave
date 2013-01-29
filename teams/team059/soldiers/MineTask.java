package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Team;
import battlecode.common.Upgrade;
import static team059.utils.Utils.*;

public class MineTask extends TravelTask {

	private MapLocation[] mines;
	
	private double density;
	
	private boolean sentMessageFlag = false;
	private int waiting = 0;
	private final int MAX_WAITING = 2;
	
	//public final double HIGH_DENSITY = 0.9;
	public Team mineAtDest = null;
	
	public MineTask(MapLocation target, int priority) {
		super(target, priority, 0);
		mineAtDest = RC.senseMine(destination);
	}
	
	@Override 
	public void update() {
		super.update();
		mineAtDest = RC.senseMine(destination);
	}
	
	//should depend on the strategy and round number, and an evaluation of how many mines there are around the target
	@Override
	public int appeal() {
//		if( mineAtDest != null) {
//			return -10000;
//		} else if(Clock.getRoundNum() < 100 && RC.getLocation().distanceSquaredTo(ALLY_HQ) < 16) {
//			return 1000;
//		} else return priority;
		//else return (int) ((1.0 - density) * 10.0);
		if(enemyRobots.length > 0) {
			return priority - 5000;
		} if(mineAtDest == ALLY_TEAM) {
			return -1000;
		}
		return priority;
	}

	@Override
	public void execute() throws GameActionException {
		//mines = RC.senseMineLocations(destination, 1, null);
		if(!sentMessageFlag) {
			messagingSystem.writeLayingMineMessage(destination);
			sentMessageFlag = true;
		}
		if(super.done()) {
			/*
			if(RC.hasUpgrade(Upgrade.PICKAXE)) {
				
			} else {
				
			}	*/
//			if(mineAtDest == null) {
			if(!done())
				RC.layMine();
//			}
		} else {
			super.execute();
		}

	}

	@Override
	public boolean done() {
		if(currentLocation.isAdjacentTo(destination)) {
			try {
				if(RC.senseObjectAtLocation(destination) != null) {
					if(waiting++ > MAX_WAITING) {
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		density = (double) mines.length / distance;
//		return density > HIGH_DENSITY;
		return mineAtDest == ALLY_TEAM;
	}

	@Override
	public String toString() {
		return "MINING AT " + currentLocation + ", WITH PRIORITY = " + priority;
	}
}
