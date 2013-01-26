package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import team059.movement.Mover;
import team059.movement.NavType;
import static team059.utils.Utils.*;

public class AttackTask extends TravelTask {
	
	private static final Mover mover = new Mover();
		
	public AttackTask(MapLocation target, int priority) {
		super(mover, target, priority, 0);
	}
	
	@Override
	public boolean done() {
		return false;
	}
	
	@Override
	public void execute() throws GameActionException {
		super.execute();
	}
	
	@Override
	public String toString() {
		return "ATTACKING TOWARD " + mover.getTarget();
	}
}