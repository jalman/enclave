package team059.soldiers;

import battlecode.common.MapLocation;
import team059.Task;
import team059.movement.Mover;
import team059.movement.NavType;

public class AttackTask extends TravelTask {
	
	private static final Mover mover = new Mover(NavType.BUG_DIG_I1);
	
	public AttackTask(MapLocation target, int priority) {
		super(mover, target, priority, 1);
	}
}
