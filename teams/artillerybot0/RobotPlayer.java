package artillerybot0;

import artillerybot0.encampment.ArtilleryBehavior;
import artillerybot0.encampment.EncampmentBehavior;
import artillerybot0.hq.HQBehavior;
import artillerybot0.soldiers.SoldierBehavior;
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
			case ARTILLERY:
				robot = new ArtilleryBehavior(rc);
				break;
			default:
				robot = new EncampmentBehavior(rc);
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
