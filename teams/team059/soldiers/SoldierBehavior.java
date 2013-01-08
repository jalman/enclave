package team059.soldiers;

import team059.RobotPlayer;
import battlecode.common.*;

public class SoldierBehavior {
	RobotController rc;
	MapLocation myBase, enemyBase;
	public SoldierBehavior(RobotController therc) {
		rc = therc;
		myBase = rc.getLocation();
		enemyBase = rc.senseEnemyHQLocation();
	}

	public void run() {
		while(true) {
			try {
				beginTurn();
				
				if (rc.isActive()) {
					Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
					Team mine = rc.senseMine(rc.getLocation().add(dir));
					if(mine != null && mine != rc.getTeam()) {
						rc.defuseMine(rc.getLocation().add(dir));
					}
					else if(rc.canMove(dir)) {
						rc.move(dir);
					}
				}
				
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
	
	public void buildEncampment(Direction dir) {
		
	}
	
	public void researchUpgrade(Upgrade upg) {
		
	}
}
