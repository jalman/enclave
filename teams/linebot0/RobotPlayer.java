package linebot0;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						// Spawn a soldier
						Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.canMove(dir))
							rc.spawn(dir);

					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
							Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
							Team mine = rc.senseMine(rc.getLocation().add(dir));
							if(mine == Team.NEUTRAL) {
								rc.defuseMine(rc.getLocation().add(dir));
							}
							else if(rc.canMove(dir)) {
								rc.move(dir);
							}
						
						
						
					}
				}

				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
