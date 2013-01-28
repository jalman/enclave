package team059.soldiers;

import team059.utils.FastIterableLocSet;
import team059.utils.Utils;
import static team059.soldiers.SoldierUtils.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import team059.RobotBehavior;
import team059.messaging.MessageHandler;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.soldiers.micro.Micro;
import static team059.utils.Utils.*;

public class NukeSoldierBehavior extends RobotBehavior {
	public Mover mover;
	
	public static Micro microSystem;
	private MineManager mineManager;
	//FastIterableLocSet mineLocs = new FastIterableLocSet();

	private TaskGiver[] taskGivers;
	private Task currentTask;
	
	public NukeSoldierBehavior() {
		mover = new Mover(NavType.BUG_HIGH_DIG);
		microSystem = new Micro();
		mineManager = new MineManager();

	}

	@Override
	public void run() throws GameActionException {
		updateSoldierUtils();
		
	}

//	@Override
//	protected MessageHandler getAttackHandler() {
//		return new MessageHandler() {
//			@Override
//			public void handleMessage(int[] message) {
//				attackManager.considerTask(new AttackTask(new MapLocation(message[0], message[1]), message[2]));
//			}
//		};
//	}
//
//	@Override
//	protected MessageHandler getTakeEncampmentHandler() {
//		return new MessageHandler() {
//			@Override
//			public void handleMessage(int[] message) {
//				takeEncampmentManager.considerTask(new ExpandTask(new MapLocation(message[0], message[1]), message[2], RobotType.values()[message[3]]));
//			}
//		};
//	}
//	@Override
//	protected MessageHandler getTakingEncampmentHandler() {
//		return new MessageHandler() {
//			@Override
//			public void handleMessage(int[] message) {
//				MapLocation loc = new MapLocation(message[0], message[1]);
//				int appeal = message[3];
//				
//				ExpandTask task = takeEncampmentManager.getTask();
//				if(task != null && loc.equals(task.destination) && appeal > task.appeal()) {
//					takeEncampmentManager.clearTask();
//					System.out.println("Decided against taking encampment.");
//				}
//			}
//		};
//	}
//	
//	protected MessageHandler getMicroHandler() {
//		return new MessageHandler() {
//			@Override
//			public void handleMessage(int[] message) {}
//		};
//	}
//	
//
	@Override
	protected MessageHandler getLayingMineHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				//int a = message[0], b = message[1];
				mineManager.receiveMineMessage(message[0], message[1]);
				//mineLocInsert(a,b,2);
//				mineLocInsert(a,b+1,1);
//				mineLocInsert(a,b-1,1);
//				mineLocInsert(a+1,b,1);
//				mineLocInsert(a-1,b,1);
			}
		};
	}
	
//	/*
//	@Override
//
//	protected MessageHandler getDefusingMineHandler() {
//		return new MessageHandler() {
//			@Override
//			public void handleMessage(int[] message) {
//				if(message[3] != ID) {
//					mineManager.receiveMineMessage(new MapLocation(message[1], message[2]));
//				}
//			}
//		};
//	}	*/
//	
//	@Override
//	protected MessageHandler getAnnounceUpgradeHandler() {
//		return new MessageHandler() {
//			@Override
//			public void handleMessage(int[] message) {
//				UPGRADES_RESEARCHED[message[1]] = true;
//			}
//		};
//	}
}
