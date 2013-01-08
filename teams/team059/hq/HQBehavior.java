package hq;

import team059.RobotPlayer;
import battlecode.common.*;

public class HQBehavior {
	public HQBehavior(RobotController rc) {
		MapLocation myBase = rc.getLocation();
		MapLocation enemyBase = rc.senseEnemyHQLocation();
	}

	public void run() {
		beginTurn();
	}
	
	public void beginTurn() {
		
	}

	public void buildSoldier(Direction dir) {
		
	}
	
	public void researchUpgrade(Upgrade upg) {
		
	}
}
