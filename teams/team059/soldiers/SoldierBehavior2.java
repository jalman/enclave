package team059.soldiers;

import team059.utils.Shields;
import team059.utils.Utils;
import static team059.soldiers.SoldierUtils.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import team059.RobotBehavior;
import team059.Strategy;
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
	
	public SoldierBehavior2() {
		mover = new Mover();
		microSystem = new Micro();
		patrolManager = new PatrolManager();
		expandManager = new ExpandManager();
		taskManager = new TaskManager();
		mineManager = new MineManager();
		attackManager = new SingleTaskManager<AttackTask>();
		takeEncampmentManager = new SingleTaskManager<ExpandTask>();
		scoutManager = new ScoutManager();
		
		taskGivers = new TaskGiver[]
				{patrolManager, taskManager, attackManager, scoutManager,
				mineManager, expandManager, takeEncampmentManager};
	}

	@Override
	public void run() throws GameActionException {

		updateSoldierUtils();
		
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
//			if(t instanceof MineTask) {
//				System.out.println("Task " + t + " has appeal " + appeal);
//			}
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
				attackManager.considerTask(new AttackTask(new MapLocation(message[0], message[1]), message[2]));
			}
		};
	}

	@Override
	protected MessageHandler getTakeEncampmentHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				takeEncampmentManager.considerTask(new ExpandTask(new MapLocation(message[0], message[1]), message[2], RobotType.values()[message[3]]));
			}
		};
	}
	@Override
	protected MessageHandler getTakingEncampmentHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				MapLocation loc = new MapLocation(message[0], message[1]);
				int appeal = message[2];
				
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
			public void handleMessage(int[] message) {}
		};
	}
	

	@Override
	protected MessageHandler getLayingMineHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				if(message[2] != ID) {
					mineManager.receiveMineMessage(message[0], message[1]);
				}
			}
		};
	}
	
	/*
	@Override

	protected MessageHandler getDefusingMineHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				if(message[2] != ID) {
					mineManager.receiveMineMessage(new MapLocation(message[0], message[1]));
				}
			}
		};
	}	*/
	
	@Override
	protected MessageHandler getAnnounceUpgradeHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				UPGRADES_RESEARCHED[message[0]] = true;
			}
		};
	}
}
