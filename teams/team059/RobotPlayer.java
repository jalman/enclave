package team059;

import team059.hq.*;
import battlecode.common.*;
import team059.soldiers.*;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		try {
			if (rc.getType() == RobotType.HQ) {
				HQBehavior hqbehave = new HQBehavior(rc);
				hqbehave.run();
			} else if (rc.getType() == RobotType.SOLDIER) {
				SoldierBehavior soldierbehave = new SoldierBehavior(rc);
				soldierbehave.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
