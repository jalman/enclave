package team059.soldiers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.Task;
import team059.utils.Utils;
import static team059.Strategy.*;

public class ExpandTask implements Task {

	private final SoldierBehavior2 soldierBehavior;
	
	public ExpandTask(SoldierBehavior2 sb) {
		soldierBehavior = sb;
	}
	
	@Override
	public int appeal() {
		return Utils.strategy.greed;
	}

	@Override
	public boolean execute() throws GameActionException {
		// TODO Auto-generated method stub
		return false;
	}

}
