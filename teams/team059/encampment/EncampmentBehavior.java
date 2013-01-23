package team059.encampment;

import team059.RobotBehavior;
import battlecode.common.GameActionException;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import static team059.utils.Utils.*;

public class EncampmentBehavior extends RobotBehavior {

	public static final int SAFETY_DISTANCE = 50;
	public static final int SAFETY_THRESHOLD = 1;
	private boolean firstTurn = true;
	
	@Override
	public void run() {
		try {
			if(firstTurn) {
				firstTurn = false;
				//System.out.println("born!");
				messagingSystem.writeBirthMessage(currentLocation, RC.getRobot().getID(), RC.getType().ordinal());
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		/*
		Robot[] enemies = RC.senseNearbyGameObjects(Robot.class, SAFETY_DISTANCE, ENEMY_TEAM);
		if(enemies.length > SAFETY_THRESHOLD && messaging) {
			try {
				messagingSystem.writeAttackMessage(RC.getLocation(), 5);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
		*/
	}
}
