package simplebot;

import simplebot.encampment.ArtilleryBehavior;
import simplebot.encampment.EncampmentBehavior;
import simplebot.hq.HQBehavior;
import simplebot.soldiers.SoldierBehavior;
import simplebot.utils.Utils;
import battlecode.common.RobotController;


public class RobotPlayer {
	public static void run(RobotController rc) {
		
		Utils.initUtils(rc);
		try {
			RobotBehavior robot = null;
			switch(rc.getType()) {
			case HQ:
				robot = new HQBehavior();
				break;
			case SOLDIER:
				robot = new SoldierBehavior();
				break;
			case ARTILLERY:
				robot = new ArtilleryBehavior();
				break;
			default:
				robot = new EncampmentBehavior();
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
