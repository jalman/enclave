package team059.soldiers;

import team059.utils.Shields;
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
	private PatrolManager patrolManager;
	private ExpandManager expandManager;
	private MineManager mineManager;
	private ScoutManager scoutManager;
	private DefuseManager defuseManager;
	
	private SingleTaskManager<AttackTask> attackManager;
	private SingleTaskManager<ExpandTask> takeEncampmentManager;
	private SingleTaskManager<AttackTask> attackEnemyHQGiver;
	
	private TaskGiver[] taskGivers;
	private final TaskGiver[] normalTaskGivers;
	private final TaskGiver[] nuclearTaskGivers;
	private final TaskGiver[] rushTaskGivers;
	private Task currentTask;

	public static Micro microSystem;
	
	public SoldierBehavior2() {
		microSystem = new Micro();
		patrolManager = new PatrolManager();
		expandManager = new ExpandManager();
		mineManager = new MineManager();
		attackManager = new SingleTaskManager<AttackTask>();
		takeEncampmentManager = new SingleTaskManager<ExpandTask>();
		scoutManager = new ScoutManager();
		attackEnemyHQGiver = new SingleTaskManager<AttackTask>(new AttackEnemyHQTask());
		defuseManager = new DefuseManager();
		
		normalTaskGivers = new TaskGiver[]
				{patrolManager, attackManager, scoutManager, defuseManager,
				mineManager, expandManager, takeEncampmentManager, attackEnemyHQGiver};
		nuclearTaskGivers = new TaskGiver[] 
				{attackManager, mineManager, expandManager, takeEncampmentManager};
		rushTaskGivers = new TaskGiver[] 
				{attackManager, expandManager, takeEncampmentManager, attackEnemyHQGiver, defuseManager};
	}

	@Override
	public void beginRound() throws GameActionException {
		updateSoldierUtils();
		super.beginRound();
	}
	
	@Override
	public void run() throws GameActionException {
		switch(strategy) {
		case NUCLEAR:
			taskGivers = nuclearTaskGivers;
			break;
		case NORMAL:
			taskGivers = normalTaskGivers;
			break;
		case RUSH:
			taskGivers = rushTaskGivers;
			break;
		default:
			taskGivers = normalTaskGivers;
		}
		
		boolean compute = false;
		int max_appeal = Integer.MIN_VALUE;
		
		if(currentTask == null) {
			compute = true;
		} else {
			currentTask.update();
			if(currentTask.done()) {
				compute = true;
				currentTask = null;
			} else {
				max_appeal = currentTask.appeal();
			}
		}

		for(int i = 0; i < taskGivers.length; i++) {
			TaskGiver tg = taskGivers[i];
			if(compute || Clock.getRoundNum() % taskGivers.length == i) {
				tg.compute();
			}
			Task t = tg.getTask();
			if(t == null || t == currentTask) continue;
			t.update();
			int appeal = t.appeal();
			if(appeal > max_appeal) {
				currentTask = t;
				max_appeal = appeal;
			}
		}
		
		if(currentTask != null && RC.isActive()) {
			RC.setIndicatorString(1, Clock.getRoundNum() + ": " + currentTask.toString() + " with appeal " + currentTask.appeal());
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
					//System.out.println("Decided against taking encampment.");
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
	
	@Override
	protected MessageHandler getDefusingMineHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				Mines.defuse[message[0]][message[1]] = Clock.getRoundNum();
			}
		};		
	}

}
