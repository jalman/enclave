package team059;

import team059.utils.MessagingSystem;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public abstract class RobotBehavior {
	protected RobotController rc;
	protected MapLocation myBase, enemyBase;
	protected MessagingSystem messagingSystem;
	
	public RobotBehavior(RobotController rc) {
		this.rc = rc;
		myBase = rc.senseHQLocation();
		enemyBase = rc.senseEnemyHQLocation();
		messagingSystem = new MessagingSystem(rc);
	}
	
	/**
	 * Called every round.
	 */
	public abstract void run();
}
