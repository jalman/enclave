package team059.hq;

import static team059.utils.Utils.*;
import team059.Strategy;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.Upgrade;

/**
 * Directs combat!
 * @author vlad
 *
 */
public class WarSystem {
	static final int NORMAL_ADVANCE_THRESHOLD = -10;
	static final int PANIC_ADVANCE_THRESHOLD = -20;
	
	final HQBehavior hq;
	
	private boolean enemyNukeHalfDone = false;
	private int enemyNukeHalfRound;
	private boolean nukePanic = false;
	
	private Robot[] allAlliedRobots, allEnemyRobots;
	
	private int advanceThreshold = NORMAL_ADVANCE_THRESHOLD;
	
	public WarSystem(HQBehavior hq) {
		this.hq = hq;
	}
	
	public void run() throws GameActionException {
		sense();
		
		if(nukePanic()) {
			advanceThreshold = PANIC_ADVANCE_THRESHOLD;
		}
		
		setBorder();
		
		int home = defendMainPriority();
		if(home > 0) {
			//parameters.border = -2.0;
			messagingSystem.writeAttackMessage(ALLY_HQ, home);
		}
		
		messagingSystem.writeParameters(parameters);
	}

	private void sense() throws GameActionException {
		allAlliedRobots = RC.senseNearbyGameObjects(Robot.class, 10000, ALLY_TEAM);
		allEnemyRobots = RC.senseNearbyGameObjects(Robot.class, 10000, ENEMY_TEAM);
		
		if(!enemyNukeHalfDone && Clock.getRoundNum() > Upgrade.NUKE.numRounds / 2) {
			enemyNukeHalfDone = RC.senseEnemyNukeHalfDone();
			enemyNukeHalfRound = Clock.getRoundNum();
		}
	}
	
	private void setBorder() throws GameActionException {
		
		if(allEnemyRobots.length == 0) {
			if(strategy != Strategy.NUCLEAR && hq.numAboveSoldierCap() >= advanceThreshold) {
				parameters.border += 0.1;
			} else {
				parameters.border = Math.max(strategy.parameters.border, parameters.border - 0.1);
			}
		} else {
			for(Robot robot : allEnemyRobots) {
				RobotInfo info = RC.senseRobotInfo(robot);
				double position = evaluate(info.location);
				if(evaluate(info.location) < parameters.border) {
					parameters.border = position;
					break;
				}
			}
		}
	}

	/**
	 * Find enemy encampments to attack?
	 */
	private void attackTargets() {
		
	}
	
	public boolean nukePanic() {
		if(!nukePanic) {
			try {
				nukePanic = enemyNukeHalfDone && Clock.getRoundNum() - enemyNukeHalfRound + Upgrade.NUKE.numRounds / 2 > RC.checkResearchProgress(Upgrade.NUKE);
			} catch(GameActionException e) {
				e.printStackTrace();
			}
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
