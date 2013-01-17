package team059;

import team059.encampment.ArtilleryBehavior;
import team059.encampment.EncampmentBehavior;
import team059.hq.HQBehavior;
import team059.soldiers.SoldierBehavior;
import team059.soldiers.SoldierBehavior2;
import team059.utils.Utils;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc) {

		Utils.initUtils(rc);
		RobotBehavior robot = null;
		switch(rc.getType()) {
		case HQ:
			robot = new HQBehavior();
			break;
		case SOLDIER:
			try {
				robot = new SoldierBehavior();
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			robot = new SoldierBehavior2();
			break;
		case ARTILLERY:
			robot = new ArtilleryBehavior();
			break;
		default:
			robot = new EncampmentBehavior();
			break;
		}
		
		robot.initTasks();

		try {
			while(true) {
				Utils.updateUtils();
				robot.beginRound();
				robot.run();
				robot.endRound();
				rc.yield();
			}
		} catch(GameActionException e) {
			e.printStackTrace();
		}
	}
}
