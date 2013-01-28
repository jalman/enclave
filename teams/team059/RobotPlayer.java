package team059;

import team059.encampment.ArtilleryBehavior;
import team059.encampment.EncampmentBehavior;
import team059.hq.AntinukeHQBehavior;
import team059.hq.HQBehavior;
import team059.soldiers.AntinukeSoldierBehavior;
import team059.soldiers.SoldierBehavior2;
import team059.utils.Utils;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc) {

		Utils.initUtils(rc);
		RobotBehavior robot = null;
		switch(rc.getType()) {
		case HQ:
			Strategy strategy = Strategy.decide();
			try{
				robot = new HQBehavior(strategy); //strategy == Strategy.RUSH ? new AntinukeHQBehavior() : new HQBehavior(strategy);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
