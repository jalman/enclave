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

		while(true) {		
			try {
				beginTurn();


				rc.setIndicatorString(0, Double.toString(rc.getTeamPower()));
		
				rc.buildSoldier(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
				
				endTurn();		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public void beginTurn() {
		
	}
	
	public void endTurn() {
		rc.yield();
	}

	public void buildSoldier(Direction dir) {
		if (rc.isActive()) {
			// Spawn a soldier
			if (rc.canMove(dir) && rc.senseMine(rc.getLocation().add(dir)))
				rc.spawn(dir);

		}
	}
	
	public void researchUpgrade(Upgrade upg) {
		
	}
}
