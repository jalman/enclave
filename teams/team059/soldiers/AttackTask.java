package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.utils.Utils;
import static team059.utils.Utils.*;

public class AttackTask extends TravelTask {
	
	private static final Mover mover = new Mover();
	
	MapLocation m;
	
	public AttackTask(MapLocation target, int priority) {
		super(mover, target, priority, 1);
	}
	
	@Override
	public boolean done() {
		return RC.senseNearbyGameObjects(Robot.class, destination, ENEMY_RADIUS2, ENEMY_TEAM).length == 0;
	}
	
	@Override
	public void execute() throws GameActionException {
		m = SoldierUtils.findClosebyEnemy();
		if(m != null) {
			SoldierBehavior2.microSystem.enemySoldierTarget = m;
			SoldierBehavior2.microSystem.run();
		} else {
			super.execute();
		}
	}
}
