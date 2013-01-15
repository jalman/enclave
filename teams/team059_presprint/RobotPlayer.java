package team059_presprint;

import team059_presprint.encampment.ArtilleryBehavior;
import team059_presprint.encampment.EncampmentBehavior;
import team059_presprint.hq.HQBehavior;
import team059_presprint.soldiers.SoldierBehavior;
import team059_presprint.utils.Utils;
import battlecode.common.RobotController;


public class RobotPlayer {
	public static void run(RobotController rc) {
		
		Utils.initUtils(rc);
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
				robot.beginRound();
				robot.run();
				robot.endRound();
				rc.yield();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
