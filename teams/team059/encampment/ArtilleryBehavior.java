package team059.encampment;

import battlecode.common.GameActionException;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import team059.RobotBehavior;

public class ArtilleryBehavior extends RobotBehavior {

	public ArtilleryBehavior(RobotController rc) {
		super(rc);
	}

	@Override
	public void run() {
		Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, rc.getType().attackRadiusMaxSquared, enemy());
		if(!rc.isActive())
			return;
		if(enemies.length > 0) {
			try {
				RobotInfo ri = rc.senseRobotInfo(enemies[0]);
				rc.attackSquare(ri.location);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public Team enemy() {
		return rc.getTeam() == Team.A ? Team.B : Team.A;
	}

}