package team059.hq;

import static team059.utils.Utils.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
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
			messagingSystem.writeParameters(parameters);
		}
	}


	public boolean nukePanic() throws GameActionException {
		if(!nukePanic) {
			nukePanic = enemyNukeHalfDone && Clock.getRoundNum() - enemyNukeHalfRound + Upgrade.NUKE.numRounds / 2 > RC.checkResearchProgress(Upgrade.NUKE);
		}
		return nukePanic;
	}
}
