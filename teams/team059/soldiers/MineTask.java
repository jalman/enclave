package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Upgrade;
import static team059.utils.Utils.*;

public class MineTask extends TravelTask {

	private MapLocation[] mines;
	
	private double density;
	
	public MineTask(SoldierBehavior2 sb) {
		super(sb.mover, currentLocation, 0, 0);
	}
	
	//should depend on the strategy and round number, and an evaluation of how many mines there are around the target
	@Override
	public int appeal() {
		if( RC.senseMine(currentLocation) != null) {
			return -10000;
		} else if(Clock.getRoundNum() < 100 && RC.getLocation().distanceSquaredTo(ALLY_HQ) < 16) {
			return 1000;
		}
		else return (int) ((1.0 - density) * 20.0);
		
	}

	@Override
	public void execute() throws GameActionException {
		if(super.done()) {
			/*
			if(RC.hasUpgrade(Upgrade.PICKAXE)) {
				
			} else {
				
			}	*/
			if(RC.senseMine(currentLocation) == null) {
				RC.layMine();
			}
		} else {
			super.execute();
		}

	}

	@Override
	public boolean done() {
		mines = RC.senseMineLocations(destination, distance, null);
		density = (double) mines.length / distance;
		return density > 0.9;
	}

}
