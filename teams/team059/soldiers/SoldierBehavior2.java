package team059.soldiers;

import team059.utils.Utils;
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
	private MineManager mineManager;
		
	public static Micro microSystem;
	private SingleTaskManager attackManager;
	private SingleTaskManager takeEncampmentManager;
	private ScoutManager scoutManager;

	private TaskGiver[] taskGivers;
	private Task currentTask;
	
	public MapLocation battleSpot;
	public int battleSpotAge;

	public SoldierBehavior2() {
		mover = new Mover();
		microSystem = new Micro(this);
		patrolManager = new PatrolManager();
		expandManager = new ExpandManager();
		taskManager = new TaskManager();
		mineManager = new MineManager(this);
		attackManager = new SingleTaskManager();
		takeEncampmentManager = new SingleTaskManager();
		scoutManager = new ScoutManager();
		
		taskGivers = new TaskGiver[] {patrolManager, taskManager, mineManager, expandManager, attackManager, takeEncampmentManager, scoutManager};
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
			if(t instanceof MineTask) {
				System.out.println("Mine task " + (MineTask) t + " has appeal " + appeal);
			}
		}
		
		if(Utils.enemyRobots.length > 0)
		{
			microSystem.run();
		}
		else if(currentTask != null && RC.isActive()) {
			RC.setIndicatorString(1, currentTask.toString());
			currentTask.execute();
		}
		updateVariables();
	}

	public void updateVariables()
	{
		battleSpotAge++;
	}
	@Override
	protected MessageHandler getAttackHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				attackManager.considerTask(new AttackTask(new MapLocation(message[1], message[2]), message[3]));
			}
		};
	}

	@Override
	protected MessageHandler getTakeEncampmentHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				takeEncampmentManager.considerTask(new ExpandTask(new MapLocation(message[1], message[2]), message[3], RobotType.values()[message[4]]));
			}
		};
	}
	
	protected MessageHandler getMicroHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				if (battleSpot == null || Utils.naiveDistance(battleSpot, Utils.currentLocation) >= 7 || battleSpotAge >= 4)
				{
					battleSpot= new MapLocation(message[1], message[2]);
					battleSpotAge = 0;
				}
				
				int distance = Utils.naiveDistance(battleSpot, Utils.currentLocation);
				
				if(distance < 7 && distance > 4)
				{
					RC.setIndicatorString(2, "CHARGING TO " + battleSpot + " on turn " + Clock.getRoundNum());
					mover.setTarget(battleSpot);
				}
				else if (distance <= 4)
				{
					try {
						microSystem.run();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				else if (distance <= 4)
//				{
//					try {
//						microSystem.run();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				//taskManager.insertTask(new MicroTask(new MapLocation(message[1], message[2]), message[3]));
			}
		};
	}
}
