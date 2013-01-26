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
	public Mover mover;

	private PatrolManager patrolManager;
	private ExpandManager expandManager;
	private TaskManager taskManager;
	private MineManager mineManager;
	private ScoutManager scoutManager;
		
	public static Micro microSystem;
	private SingleTaskManager<AttackTask> attackManager;
	private SingleTaskManager<ExpandTask> takeEncampmentManager;

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
		attackManager = new SingleTaskManager<AttackTask>();
		takeEncampmentManager = new SingleTaskManager<ExpandTask>();
		scoutManager = new ScoutManager();
		
		taskGivers = new TaskGiver[]
				{patrolManager, taskManager, attackManager, scoutManager,
				/*mineManager,*/ expandManager, takeEncampmentManager};
	}

	@Override
	public void run() throws GameActionException {
		boolean compute = currentTask == null || currentTask.done();

		int max_appeal = Integer.MIN_VALUE;

		for(int i = 0; i < taskGivers.length; i++) {
			TaskGiver tg = taskGivers[i];
			if(compute || Clock.getRoundNum() % taskGivers.length == i) {
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
	
	@Override
	protected MessageHandler getTakingEncampmentHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				MapLocation loc = new MapLocation(message[1], message[2]);
				int appeal = message[3];
				
				ExpandTask task = takeEncampmentManager.getTask();
				if(task != null && loc.equals(task.destination) && appeal > task.appeal()) {
					takeEncampmentManager.clearTask();
					System.out.println("Decided against taking encampment.");
				}
			}
		};
	}
	
	protected MessageHandler getMicroHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				if (battleSpot == null || battleSpotAge >= 4 || Utils.naiveDistance(battleSpot, Utils.currentLocation) >= 7)
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
			}
		};
	}
}
