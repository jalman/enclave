package artillerybot1;

import artillerybot1.encampment.ArtilleryBehavior;
import artillerybot1.encampment.EncampmentBehavior;
import artillerybot1.hq.HQBehavior;
import artillerybot1.soldiers.SoldierBehavior;
import artillerybot1.utils.Utils;
import battlecode.common.RobotController;


public class RobotPlayer {
	public static void run(RobotController rc) {
		try {
			Utils.initUtils(rc);
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
