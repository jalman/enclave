package team059;

import team059.encampment.ArtilleryBehavior;
import team059.encampment.EncampmentBehavior;
import team059.hq.HQBehavior;
import team059.soldiers.SoldierBehavior2;
import team059.utils.Utils;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc) {
		rc.setIndicatorString(0, "HACK");
		rc.setIndicatorString(1, "PUNT");
		rc.setIndicatorString(2, "NUKE");
		Utils.initUtils(rc);
		RobotBehavior robot = null;
		switch(rc.getType()) {
		case HQ:
			Strategy strategy = Strategy.decide();
			robot = new HQBehavior(strategy);
			break;
		case SOLDIER:
			robot = new SoldierBehavior2();
			break;
		case ARTILLERY:
			robot = new ArtilleryBehavior();
			break;
		default:
			robot = new EncampmentBehavior();
			break;
		}

		while(true) {
			try {
				Utils.updateUtils();
				robot.beginRound();
				robot.run();
				robot.endRound();
				rc.yield();
			} catch(GameActionException e) {
				System.out.println("Round number = " + Clock.getRoundNum());
				e.printStackTrace();
			}
		}
	}
}
