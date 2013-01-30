package team059.hq;

import static team059.utils.Utils.*;

import java.util.Arrays;

import team059.RobotBehavior;
import team059.Strategy;
import team059.utils.ArraySet;
import team059.utils.Utils;
import battlecode.common.*;

public class NukeHQBehavior extends RobotBehavior {
	Strategy strategy;
	int buildOrderProgress = 0;
	boolean enemyNukeHalfDone = false;
	int enemyNukeHalfRound;
	int numBots;
	
	public enum buildOrderStep {SOLDIER, FUSION, DEFUSION, PICKAXE, VISION, NUKE};
	public buildOrderStep[] buildOrder;
	
	boolean panicSoldiers = false;
	
	public NukeHQBehavior(Strategy strategy) {
		this.strategy = strategy;
		buildOrder = new buildOrderStep[] 
				{buildOrderStep.PICKAXE, buildOrderStep.SOLDIER, 
				buildOrderStep.SOLDIER, buildOrderStep.SOLDIER, 
				buildOrderStep.SOLDIER, buildOrderStep.NUKE};
	}
	
	@Override
	public void beginRound() throws GameActionException {

		try {
			messagingSystem.beginRoundHQ(messageHandlers);
		} catch (GameActionException e1) {
			e1.printStackTrace();
		}

		if(!enemyNukeHalfDone && Clock.getRoundNum() > Upgrade.NUKE.numRounds / 2) {
			enemyNukeHalfDone = RC.senseEnemyNukeHalfDone();
			enemyNukeHalfRound = Clock.getRoundNum();
		}
	}

	/**
	 * Handle upgrades and robots.
	 */
	private void macro() {
		boolean done = false;
		if(buildOrderProgress < buildOrder.length) {
			try {
				if(RC.isActive()) {
					switch(buildOrder[buildOrderProgress]) {
					case FUSION:
						done = researchUpgrade(Upgrade.FUSION);
						break;
					case DEFUSION:
						done = researchUpgrade(Upgrade.DEFUSION);
						break;
					case VISION:
						done = researchUpgrade(Upgrade.VISION);
						break;
					case PICKAXE:
						done = researchUpgrade(Upgrade.PICKAXE);
						break;
					case NUKE:
						done = researchUpgrade(Upgrade.NUKE);
						break;
					case SOLDIER:
						done = buildSoldier();
						break;
					default:
					}
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
		if(done) {
//			System.out.println("Finished " + buildOrder[buildOrderProgress]);
			buildOrderProgress++;
			if(RC.isActive()) {
				macro();
			}
		}
	}

	@Override
	public void run() throws GameActionException {
		macro();
		//expand();
		
//		if(panic()) {
//			messagingSystem.writeAttackMessage(ENEMY_HQ, 500000);
//		}
	}
//	
//	public boolean panic() {
//		try {
//			if(!panicking) {
//				panicking = enemyNukeHalfDone && Clock.getRoundNum() - enemyNukeHalfRound + Upgrade.NUKE.numRounds / 2 > RC.checkResearchProgress(Upgrade.NUKE);
//			}
//		} catch (GameActionException e) {
//			e.printStackTrace();
//		}
//		return panicking;
//	}
	
	/**
	 * Tries to build a soldier.
	 * @return Whether successful.
	 * @throws GameActionException
	 */
	boolean buildSoldier() throws GameActionException {
		return buildSoldier(ALLY_HQ.directionTo(ENEMY_HQ));
	}
	
	/**
	 * Tries to build a soldier.
	 * @param dir The direction in which to build.
	 * @return Whether successful.
	 * @throws GameActionException
	 */
	boolean buildSoldier(Direction dir) throws GameActionException {
		//if (RC.isActive()) {
			// Spawn a soldier
			for(int i = 0; i < 8; i++) {
				if(goodPlaceToMakeSoldier(dir)) {
					RC.spawn(dir);
					messagingSystem.writeStrategy(strategy);
					return true;
				}
				dir = dir.rotateRight();
			}
			//message guys to get out of the way??
		//}
		return false;
	}

	private boolean goodPlaceToMakeSoldier(Direction dir) {
		return RC.canMove(dir) && !Utils.isEnemyMine(RC.getLocation().add(dir));
	}

	boolean researchUpgrade(Upgrade upg) throws GameActionException {
		if(RC.hasUpgrade(upg)) return true;
		//if(RC.isActive()) {
		RC.researchUpgrade(upg);
		//}
		return false;
	}
}
