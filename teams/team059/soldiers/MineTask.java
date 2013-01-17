package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.Task;

public class MineTask implements Task {

	private final SoldierBehavior2 soldierBehavior;
	
	private final MapLocation target;
	
	public MineTask(SoldierBehavior2 sb, MapLocation target) {
		soldierBehavior = sb;
		this.target = target;
	}
	
	//should depend on the strategy and round number, and an evaluation of how many mines there are around the target
	@Override
	public int appeal() {
		
		return Clock.getRoundNum() < 100 ? 10 : 5;
	}

	@Override
	public boolean execute() throws GameActionException {
		// TODO Auto-generated method stub
		return false;
	}

}
