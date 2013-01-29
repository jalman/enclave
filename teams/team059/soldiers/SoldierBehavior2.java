package team059.soldiers;

import team059.utils.Shields;
import static team059.soldiers.SoldierUtils.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.Upgrade;
import team059.Encampments;
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
	private SingleTaskManager<DodgeMineTask> dodgeMineManager;
	
	private TaskGiver[] taskGivers;
	private final TaskGiver[] normalTaskGivers;
	private final TaskGiver[] nuclearTaskGivers;
	private final TaskGiver[] rushTaskGivers;
	private Task currentTask;

	public static Micro microSystem;
	
	public static int bornRound;
	public static int soldierID = -1;
	
	public SoldierBehavior2() {
		microSystem = new Micro();
		patrolManager = new PatrolManager();
		expandManager = new ExpandManager();
		mineManager = new MineManager();
		attackManager = new SingleTaskManager<AttackTask>();
		takeEncampmentManager = new SingleTaskManager<ExpandTask>();
		scoutManager = new ScoutManager();
		//defuseManager = new DefuseManager();
		dodgeMineManager = new SingleTaskManager<DodgeMineTask>(new DodgeMineTask());
		
		normalTaskGivers = new TaskGiver[]
				{patrolManager, attackManager, scoutManager,
				mineManager, expandManager, takeEncampmentManager, dodgeMineManager};
		nuclearTaskGivers = new TaskGiver[] 
				{patrolManager, attackManager, mineManager, expandManager, takeEncampmentManager, dodgeMineManager};
		rushTaskGivers = new TaskGiver[] 
				{patrolManager, attackManager, expandManager, takeEncampmentManager, dodgeMineManager};
		
		bornRound = Clock.getRoundNum();
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
			//System.out.print("Task " + t + " has appeal " + appeal + " || ");
			if(appeal > max_appeal) {
				RC.setIndicatorString(0, "Task giver " + i);
				currentTask = t;
				max_appeal = appeal;
			}
		}
		//System.out.println();
		
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
				Encampments.claim(message[0], message[1], message[2]);
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
				mineManager.receiveMineMessage(message[0], message[1]);
			}
		};
	}
	
	@Override
	protected MessageHandler getAnnounceUpgradeHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				UPGRADES_RESEARCHED[message[0]] = true;
				
				if(message[0] == Upgrade.PICKAXE.ordinal()) {
					mineManager.resetMineCenter();
				}
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

	@Override
	protected MessageHandler getSoldierIDHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				if(Clock.getRoundNum() - bornRound < 2) {
					soldierID = message[0];
					//System.out.println("I'm soldier " + soldierID + "!");
					mineManager.setSoldierID(soldierID);
				}
			}
		};
	}
	
}
