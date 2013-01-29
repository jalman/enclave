package team059.soldiers;

import preSprintBot.Strategy;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.movement.Mover;
import static team059.soldiers.SoldierUtils.*;
import static team059.utils.Utils.*;

public class AttackTask extends TravelTask {

	private static final Mover mover = new Mover();
	public AttackTask(MapLocation target, int priority) {
		super(mover, target, priority, 2);
	}

	@Override
	public boolean done() {
		if(enemyRobots.length > 0) return false;
		return super.done();
	}

	@Override
	public void execute() throws GameActionException {
		if(farawayEnemyTarget != null) {
			SoldierBehavior2.microSystem.run();
		} else if(!Mines.tryDefuse(destination, true)) {
			super.execute();
		}
	}

	@Override
	public String toString() {
		return "ATTACKING TOWARD " + destination;
	}
}
