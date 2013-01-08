package team059.hq;

import team059.RobotBehavior;
import team059.RobotPlayer;
import battlecode.common.*;

public class HQBehavior extends RobotBehavior {

	public HQBehavior(RobotController therc) {
		super(therc);
	}

	@Override
	public void run() {
		try {
			rc.setIndicatorString(0, Double.toString(rc.getTeamPower()));
			buildSoldier(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void beginTurn() {

	}

	public void endTurn() {
		rc.yield();
	}

	public void buildSoldier(Direction dir) throws GameActionException {
		if (rc.isActive()) {
			// Spawn a soldier
			if (rc.canMove(dir) && rc.senseMine(rc.getLocation().add(dir)) == null)
				rc.spawn(dir);
		}
	}

	public void researchUpgrade(Upgrade upg) {

	}
}
