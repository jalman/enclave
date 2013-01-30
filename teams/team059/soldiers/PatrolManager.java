package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;
import static team059.utils.Utils.*;
import static team059.soldiers.PrioritySystem.PATROL_PRIORITY;;

/**
 * Patrol near the frontier.
 * @author vlad
 *
 */
public class PatrolManager extends TaskGiver {
	
	private static final int STEP_SIZE = 4;
	
	private AttackTask attackTask;
	
	@Override
	public void compute() throws GameActionException {		
		if(isDangerous(currentLocation)) {
			retreat();
		} else if(isSafe(currentLocation)) {
			advance();
		} else {
			patrol();
		}
	}

	private void retreat() {
		Direction dir = currentLocation.directionTo(ALLY_HQ);
		attackTask = new AttackTask(currentLocation.add(dir, STEP_SIZE), PATROL_PRIORITY, true);
	}
	
	private void advance() {
		Direction dir = currentLocation.directionTo(ENEMY_HQ);
		attackTask = new AttackTask(currentLocation.add(dir, STEP_SIZE), PATROL_PRIORITY, true);
	}
	
	/**
	 * Move in a random direction.
	 */
	private void patrol() {
		Direction dir = currentLocation.directionTo(ENEMY_HQ);
		if(random.nextBoolean()) {
			dir = dir.rotateLeft().rotateLeft();
		} else {
			dir = dir.rotateRight().rotateRight();
		} 
		
		MapLocation patrolTarget = currentLocation.add(dir, STEP_SIZE);
		if(RC.senseTerrainTile(patrolTarget) == TerrainTile.OFF_MAP) patrolTarget = currentLocation.add(dir.opposite(), STEP_SIZE);
		
		attackTask = new AttackTask(patrolTarget, PATROL_PRIORITY, true);
	}
	
	@Override
	public Task getTask() {
		return attackTask;
	}

}
