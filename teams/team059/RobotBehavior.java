package team059;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public abstract class RobotBehavior {
	protected RobotController rc;
	protected MapLocation myBase, enemyBase;
	
	public RobotBehavior(RobotController rc) {
		this.rc = rc;
		myBase = rc.senseHQLocation();
		enemyBase = rc.senseEnemyHQLocation();
	}
	
	/**
	 * Called every round.
	 */
	public abstract void run();
	
}
