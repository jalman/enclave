package preSprintBot.encampment;

import preSprintBot.RobotBehavior;
import battlecode.common.GameActionException;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import static preSprintBot.utils.Utils.*;

public class EncampmentBehavior extends RobotBehavior {

	public static final int SAFETY_DISTANCE = 50;
	public static final int SAFETY_THRESHOLD = 1;
	
	public EncampmentBehavior(RobotController rc) {
		super(rc);
	}
	
	@Override
	public void run() {
		Robot[] enemies = RC.senseNearbyGameObjects(Robot.class, SAFETY_DISTANCE, ENEMY_TEAM);
		if(enemies.length > SAFETY_THRESHOLD && messaging) {
			try {
				messagingSystem.writeAttackMessage(RC.getLocation(), 5);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}
}
