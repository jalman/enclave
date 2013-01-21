package movertest;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		switch(rc.getType()) {
		case HQ:
			for(Direction d : Direction.values()) {
				if (rc.canMove(d)) {
					try {
						rc.spawn(d);
					} catch (GameActionException e) {
						e.printStackTrace();
					}
					break;
				}
			}
			while(true) {
				rc.yield();
			}
		case SOLDIER:
			Utils.initUtils(rc);
			Mover mover = new Mover();
			mover.setTarget(rc.senseEnemyHQLocation());
			while (true) {
				try {
					mover.execute();
					rc.yield();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
