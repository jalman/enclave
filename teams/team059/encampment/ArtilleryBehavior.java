package team059.encampment;

import battlecode.common.GameActionException;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import team059.RobotBehavior;
import team059.utils.Utils;

public class ArtilleryBehavior extends RobotBehavior {

	public ArtilleryBehavior(RobotController rc) {
		super(rc);
	}

	@Override
	public void run() {
		Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, rc.getType().attackRadiusMaxSquared, Utils.ENEMY_TEAM);
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
}