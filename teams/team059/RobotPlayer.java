package team059;

import preSprintBot.encampment.ArtilleryBehavior;
import preSprintBot.encampment.EncampmentBehavior;
import preSprintBot.hq.HQBehavior;
import preSprintBot.soldiers.SoldierBehavior;
import preSprintBot.utils.Utils;
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
