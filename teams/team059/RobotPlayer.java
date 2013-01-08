package team059;

import team059.hq.HQBehavior;
import team059.soldiers.SoldierBehavior;
import battlecode.common.RobotController;


public class RobotPlayer {
	public static void run(RobotController rc) {
		try {
			RobotBehavior robot = null;
			switch(rc.getType()) {
			case HQ:
				robot = new HQBehavior(rc);
				break;
			case SOLDIER:
				robot = new SoldierBehavior(rc);
				break;
			}
			while(true) {
				robot.run();
				rc.yield();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
