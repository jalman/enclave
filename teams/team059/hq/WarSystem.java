package team059.hq;

import static team059.utils.Utils.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Robot;
import battlecode.common.Upgrade;

/**
 * Directs combat!
 * @author vlad
 *
 */
public class WarSystem {
	private boolean enemyNukeHalfDone = false;
	private int enemyNukeHalfRound;
	private boolean nukePanic = false;

	public void run() throws GameActionException {
		if(!enemyNukeHalfDone && Clock.getRoundNum() > Upgrade.NUKE.numRounds / 2) {
			enemyNukeHalfDone = RC.senseEnemyNukeHalfDone();
			enemyNukeHalfRound = Clock.getRoundNum();
		}
		
		if(nukePanic()) {
			parameters.attack = 100;
			parameters.border = 2.0;
			messagingSystem.writeParameters(parameters);
			messagingSystem.writeAttackMessage(ENEMY_HQ, 100);
		}
		
		int home = defendMainPriority();
		if(home > 0) {
			messagingSystem.writeAttackMessage(ALLY_HQ, home);
		}
	}


	public boolean nukePanic() throws GameActionException {
		if(!nukePanic) {
			nukePanic = enemyNukeHalfDone && Clock.getRoundNum() - enemyNukeHalfRound + Upgrade.NUKE.numRounds / 2 > RC.checkResearchProgress(Upgrade.NUKE);
		}
		return nukePanic;
	}
	
	public int defendMainPriority() {
		Robot[] nearbyEnemies = RC.senseNearbyGameObjects(Robot.class, 10, ENEMY_TEAM);
		if(nearbyEnemies.length == 0) {
			return 0;
		} else if (nearbyEnemies.length < 3) {
			return 20;
		}
			return 60;
	}
}
