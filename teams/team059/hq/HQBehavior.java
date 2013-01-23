package team059.hq;

import static team059.utils.Utils.*;
import team059.RobotBehavior;
import team059.Strategy;
import team059.messaging.MessagingSystem;
import team059.utils.Utils;
import battlecode.common.*;

public class HQBehavior extends RobotBehavior {
	
	HQAction[] buildOrder;
	int buildOrderProgress = 0;
	boolean enemyNukeHalfDone = false;
	int enemyNukeHalfRound;
	
	ExpandSystem expandSystem;
	
	public HQBehavior() {
		strategy = Strategy.decide();
		buildOrder = strategy.buildOrder;	
		expandSystem = new ExpandSystem();
	}
	
	@Override
	public void beginRound() throws GameActionException {		
		messaging = RC.getTeamPower() > MessagingSystem.MESSAGING_COST;
		//messaging = false;
		if(messaging) {
			try {
				messagingSystem.beginRoundHQ();
			} catch (GameActionException e1) {
				e1.printStackTrace();
			}
			messagingSystem.handleMessages(messageHandlers);
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
		if(buildOrderProgress < buildOrder.length) {
			try {
				if(buildOrder[buildOrderProgress].execute(this)) {
					buildOrderProgress++;
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		} else if(RC.isActive()) {
			if(Clock.getRoundNum() < 100 || (RC.getTeamPower() - 40.0 > 15.0)) {
				try {
					RC.setIndicatorString(0, Double.toString(RC.getTeamPower()) + "  asdf");
					buildSoldier();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					researchUpgrade(Upgrade.NUKE);
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void expand() {
		if(RC.senseCaptureCost() + 10 < RC.getTeamPower()) {
			try {
				expandSystem.considerExpanding(0); //fix this
			} catch (GameActionException e) {
				e.printStackTrace();
			} 
		}
	}
	
	@Override
	public void run() throws GameActionException {
		macro();
		expand();
		
		if(panic()) {
			messagingSystem.writeAttackMessage(ENEMY_HQ, 5000);
		}
	}
	
	public boolean panic() {
		try {
			return enemyNukeHalfDone && Clock.getRoundNum() - enemyNukeHalfRound + Upgrade.NUKE.numRounds / 2 > RC.checkResearchProgress(Upgrade.NUKE);
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
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
		if (RC.isActive()) {
			// Spawn a soldier
			for(int i = 0; i < 8; i++) {
				if(goodPlaceToMakeSoldier(dir)) {
					RC.spawn(dir);
					messagingSystem.writeHQMessage(strategy);
					return true;
				}
				dir = dir.rotateRight();
			}
			//message guys to get out of the way??
		}
		return false;
	}

	private boolean goodPlaceToMakeSoldier(Direction dir) {
		return RC.canMove(dir) && !Utils.isEnemyMine(RC.getLocation().add(dir));
	}

	void researchUpgrade(Upgrade upg) throws GameActionException {
		if(RC.isActive()) {
			RC.researchUpgrade(upg);
		}
	}

}
