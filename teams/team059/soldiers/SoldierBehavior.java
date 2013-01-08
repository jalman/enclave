package team059.soldiers;

import team059.RobotBehavior;
import battlecode.common.*;

public class SoldierBehavior extends RobotBehavior {

	public SoldierBehavior(RobotController therc) {
		super(therc);
	}

	@Override
	public void run() {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void buildEncampment(Direction dir) {

	}

	public void researchUpgrade(Upgrade upg) {

	}
}
