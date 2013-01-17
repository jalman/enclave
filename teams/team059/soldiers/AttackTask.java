package team059.soldiers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.Task;

public class AttackTask implements Task {

	private final SoldierBehavior2 soldierBehavior;
	
	private final MapLocation target;
	private final int priority;
	
	public AttackTask(SoldierBehavior2 sb, MapLocation target, int priority) {
		soldierBehavior = sb;
		this.target = target;
		this.priority = priority;
	}
	
	@Override
	public int appeal() {
		return priority;
	}

	@Override
	public boolean execute() throws GameActionException {
		// TODO Auto-generated method stub
		return false;
	}

}
