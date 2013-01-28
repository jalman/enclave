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
	private final int MAX_WAITING = 4;
	
	//public final double HIGH_DENSITY = 0.9;
	public Team mineHere = null;
	
	public MineTask(MapLocation target, int priority) {
		super(target, priority, 0);
		//System.out.println("New MineTask with target " + target + ", priority " + priority);
	}
	
	//should depend on the strategy and round number, and an evaluation of how many mines there are around the target
	@Override
	public int appeal() {
//		if( mineHere != null) {
//			return -10000;
//		} else if(Clock.getRoundNum() < 100 && RC.getLocation().distanceSquaredTo(ALLY_HQ) < 16) {
//			return 1000;
//		} else return priority;
		//else return (int) ((1.0 - density) * 10.0);
		if(mineHere != null) {
			return -1000+priority;
		}
		return priority;
	}

	@Override
	public void execute() throws GameActionException {
		//mines = RC.senseMineLocations(destination, 1, null);
		mineHere = RC.senseMine(destination);
		if(!sentMessageFlag) {
			messagingSystem.writeLayingMineMessage(destination, ID);
			sentMessageFlag = true;
		}
		if(super.done()) {
			/*
			if(RC.hasUpgrade(Upgrade.PICKAXE)) {
				
			} else {
				
			}	*/
//			if(mineHere == null) {
				RC.layMine();
//			}
		} else {
			super.execute();
		}

	}

	@Override
	public boolean done() {
		mineHere = RC.senseMine(destination);
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
		return mineHere == ALLY_TEAM;
	}

	@Override
	public String toString() {
		return "MINING AT " + currentLocation + ", WITH PRIORITY = " + priority;
	}
}
