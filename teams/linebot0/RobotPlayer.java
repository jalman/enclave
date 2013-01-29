package linebot0;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
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
		Team ALLY = rc.getTeam();
		Team ENEMY = ALLY.opponent();
		while (true) {
			MapLocation here = rc.getLocation();
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						// Spawn a soldier
						if(!rc.hasUpgrade(Upgrade.DEFUSION)) {
							rc.researchUpgrade(Upgrade.DEFUSION);
						} else {
							Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
							Direction tryDir = dir;
							do {
								if (rc.canMove(tryDir) && rc.senseMine(here.add(tryDir)) == null) {
									rc.spawn(tryDir);
									break;
								}
								tryDir = tryDir.rotateLeft();
							} while(!tryDir.equals(dir));
						}
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					//System.out.println("(" + (Clock.getRoundNum() - 20) + ", " + (Clock.getRoundNum() - 20) + "): " + rc.senseTerrainTile(new MapLocation(Clock.getRoundNum() - 20, Clock.getRoundNum() - 20)));
					if (rc.isActive()) {
							Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
							Team mine = rc.senseMine(rc.getLocation().add(dir));
							if(mine == Team.NEUTRAL || mine == ENEMY) {
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
