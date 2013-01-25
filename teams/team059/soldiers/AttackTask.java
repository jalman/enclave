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
	private int turnsSinceSeenEnemy = 0;
	private boolean engaged;
	
	MapLocation m;
	
	public AttackTask(MapLocation target, int priority) {
		super(mover, target, priority, 0);
	}
	
	@Override
	public boolean done() {
		if(engaged) {
			if((turnsSinceSeenEnemy > 5) && RC.senseNearbyGameObjects(Robot.class, destination, ENEMY_RADIUS2, ENEMY_TEAM).length == 0) {
				engaged = false;
				turnsSinceSeenEnemy = 0;				
				return mover.getTarget().distanceSquaredTo(currentLocation) <= this.distance;
			}
		}
		return false;
	}
	
	@Override
	public void execute() throws GameActionException {
		m = SoldierUtils.findClosebyEnemy();
		if(m != null) {
			if(!engaged) {
				turnsSinceSeenEnemy = 0;
				engaged = true;
			}
//			SoldierBehavior2.microSystem.enemySoldierTarget = m;
//			SoldierBehavior2.microSystem.run();
		} else {
			turnsSinceSeenEnemy++;
			super.execute();
		}
	}
	
	@Override
	public String toString() {
		return "ATTACKING TOWARD " + mover.getTarget();
	}
}
