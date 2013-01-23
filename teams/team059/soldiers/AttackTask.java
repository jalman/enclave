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
	
	
	public AttackTask(MapLocation target, int priority) {
		super(mover, target, priority, 1);
	}
	
	@Override
	public boolean done() {
		return RC.senseNearbyGameObjects(Robot.class, destination, ENEMY_RADIUS2, ENEMY_TEAM).length == 0;
	}
	
	@Override
	public void execute() throws GameActionException {
		if(SoldierUtils.findClosebyEnemy() != null) {
			int k = Clock.getBytecodeNum();
			SoldierBehavior2.microSystem.run();
			RC.setIndicatorString(2, Clock.getBytecodeNum()-k + "bytecode on turn" + Clock.getRoundNum());
		} else {
			super.execute();
		}
	}
}
