package nukeplayer;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Upgrade;

public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						// Spawn a soldier
						Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						for(int i = 0; i < 8; i++) {
							if (rc.canMove(dir)) {
								rc.spawn(dir);
								rc.yield();
							} else if (i == 7) {
								rc.researchUpgrade(Upgrade.NUKE);
							}
							dir = dir.rotateLeft();
						}
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						if(rc.senseMine(rc.getLocation())==null)
							rc.layMine();
					}
				}

				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
