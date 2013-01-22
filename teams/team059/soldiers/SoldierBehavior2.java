package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import team059.RobotBehavior;
import team059.messaging.MessageHandler;
import team059.movement.Mover;
import team059.soldiers.micro.Micro;
import static team059.utils.Utils.*;

public class SoldierBehavior2 extends RobotBehavior {

	public static int ENEMY_RADIUS = 5;
	public static int ENEMY_RADIUS2 = 25;
	
	public Robot[] nearbyEnemies;

	public Mover mover;

	private PatrolManager patrolManager;
	private ExpandManager expandManager;
	private TaskManager taskManager;
	public static Micro microSystem;

	private TaskGiver[] taskGivers;
	private Task currentTask;

	public SoldierBehavior2() {
		mover = new Mover();
		microSystem = new Micro();
		patrolManager = new PatrolManager();
		expandManager = new ExpandManager();
		taskManager = new TaskManager();

		taskGivers = new TaskGiver[] {patrolManager, taskManager, expandManager};
	}

	@Override
	public void beginRound() throws GameActionException {
		super.beginRound();
		nearbyEnemies = RC.senseNearbyGameObjects(Robot.class, currentLocation, ENEMY_RADIUS2, ENEMY_TEAM);
	}

	private static final int THINK_PERIOD = 20;

	@Override
	public void run() throws GameActionException {
		boolean compute = currentTask == null || currentTask.done();

		int max_appeal = Integer.MIN_VALUE;

		for(int i = 0; i < taskGivers.length; i++) {
			TaskGiver tg = taskGivers[i];
			if(compute || Clock.getRoundNum() % THINK_PERIOD == i) {
				tg.compute();
			}
			Task t = tg.getTask();
			if(t == null) continue;
			int appeal = t.appeal();
			if(appeal > max_appeal) {
				currentTask = t;
				max_appeal = appeal;
			}
		}

		if(currentTask != null && RC.isActive()) {
			RC.setIndicatorString(1, currentTask.toString());
			currentTask.execute();
		}
	}

	@Override
	protected MessageHandler getAttackHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				taskManager.insertTask(new AttackTask(new MapLocation(message[1], message[2]), message[3]));
			}
		};
	}

	@Override
	protected MessageHandler getTakeEncampmentHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				System.out.println("hi");
				taskManager.insertTask(new ExpandTask(new MapLocation(message[1], message[2]), message[3], RobotType.values()[message[4]]));
			}
		};
	}
	
	protected MessageHandler getMicroHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				taskManager.insertTask(new MicroTask(new MapLocation(message[1], message[2]), message[3]));
			}
		};
	}
}
