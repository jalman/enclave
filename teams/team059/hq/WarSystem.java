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
	final HQBehavior hq;
	
	private boolean enemyNukeHalfDone = false;
	private int enemyNukeHalfRound;
	private boolean nukePanic = false;
	
	private Robot[] allAlliedRobots, allEnemyRobots;
	private double farthest;
	
	public WarSystem(HQBehavior hq) {
		this.hq = hq;
	}
	
	public void run() throws GameActionException {
		sense();
		
		//setBorder();
		
		if(nukePanic()) {
			messagingSystem.writeAttackMessage(ENEMY_HQ, 200);
		}
		int home = defendMainPriority();
		if(home > 0) {
			parameters.border = -2.0;
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
			parameters.border += 0.003;
		} else {
			int min_distance = Integer.MAX_VALUE;
			MapLocation closest = null;
			
			for(Robot robot : allEnemyRobots) {
				RobotInfo info = RC.senseRobotInfo(robot);
				MapLocation loc = info.location;
				int d = loc.distanceSquaredTo(ALLY_HQ);
				if(d < min_distance) {
					closest = loc;
					min_distance = d;
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
