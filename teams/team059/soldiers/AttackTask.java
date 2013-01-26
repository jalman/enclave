package team059.soldiers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.movement.Mover;
import static team059.utils.Utils.*;

public class AttackTask extends TravelTask {

	private static final Mover mover = new Mover();

	public AttackTask(MapLocation target, int priority) {
		super(mover, target, priority, 0);
	}

	@Override
	public boolean done() {
		if(enemyRobots.length > 0) return false;
		return super.done();
	}

	@Override
	public void execute() throws GameActionException {
		if(enemyRobots.length > 0) {
			SoldierBehavior2.microSystem.run();
		} else {
			super.execute();
		}
	}

	@Override
	public String toString() {
		return "ATTACKING TOWARD " + destination;
	}
}