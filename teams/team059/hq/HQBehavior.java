package team059.hq;

import team059.RobotPlayer;
import battlecode.common.*;

public class HQBehavior {
	RobotController rc;
	MapLocation myBase, enemyBase;
	public HQBehavior(RobotController therc) {
		rc = therc;
		myBase = rc.getLocation();
		enemyBase = rc.senseEnemyHQLocation();
	}

	public void run() {
		try {
			while(true) {
			beginTurn();
	
			if (rc.isActive()) {
				// Spawn a soldier
				Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
				if (rc.canMove(dir))
					rc.spawn(dir);
	
			}
			
			endTurn();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void beginTurn() {
		
	}
	
	public void endTurn() {
		rc.yield();
	}

	public void buildSoldier(Direction dir) {
		
	}
	
	public void researchUpgrade(Upgrade upg) {
		
	}
}
