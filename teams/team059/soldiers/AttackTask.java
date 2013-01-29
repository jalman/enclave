package team059.soldiers;

import team059.Strategy;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.movement.Mover;
import static team059.soldiers.SoldierUtils.*;
import static team059.utils.Utils.*;

public class AttackTask extends TravelTask {

	private boolean tryToDefuse = true;
	private static final Mover mover = new Mover();
	public AttackTask(MapLocation target, int priority) {
		super(mover, target, priority, 2);
	}
	
	public AttackTask(MapLocation target, int priority, int distance) {
		super(mover, target, priority, distance);
	}
	
	@Override
	public void update() {
		super.update();
	}

	@Override
	public boolean done() {
		if(enemyRobots.length > 0) return false;
		return super.done();
	}

	@Override
	public void execute() throws GameActionException {
		if(farawayEnemyTarget != null) {
			SoldierBehavior2.microSystem.run(0);
		} else if(!Mines.tryDefuse(destination, true)) {
			super.execute();
		}
	}

	@Override
	public String toString() {
		return "ATTACKING TOWARD " + destination;
	}
}
