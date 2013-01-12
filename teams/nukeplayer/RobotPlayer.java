package nukeplayer;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class RobotPlayer {
	
	public static boolean done = false;
	public static Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.NORTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_WEST, Direction.SOUTH_EAST, Direction.OMNI };
	
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					rc.setIndicatorString(0, rc.checkResearchProgress(Upgrade.NUKE) + "");
					if (rc.isActive()) {
						if(!done) {
							for(Direction dir : dirs) {
								if(dir == Direction.OMNI) {
									done = true;
									rc.researchUpgrade(Upgrade.NUKE);
									break;
								} else if(rc.canMove(dir)) {
									rc.spawn(dir);
									break;
								}
							}
						} else {
							rc.researchUpgrade(Upgrade.NUKE);
							
						}
						
						
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						if(rc.senseMine(rc.getLocation().add(Direction.NORTH)) == Team.NEUTRAL) {
							rc.defuseMine(rc.getLocation().add(Direction.NORTH));
						} else if(rc.senseMine(rc.getLocation().add(Direction.SOUTH)) == Team.NEUTRAL) {
							rc.defuseMine(rc.getLocation().add(Direction.SOUTH));
						} else if(rc.senseMine(rc.getLocation().add(Direction.EAST)) == Team.NEUTRAL) {
							rc.defuseMine(rc.getLocation().add(Direction.EAST));
						} else if(rc.senseMine(rc.getLocation().add(Direction.WEST)) == Team.NEUTRAL) {
							rc.defuseMine(rc.getLocation().add(Direction.WEST));
						} else if(rc.senseMine(rc.getLocation())==null)
							rc.layMine();
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
