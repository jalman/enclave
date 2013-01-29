package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
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
		attackTask = new AttackTask(currentLocation.add(dir, STEP_SIZE), PATROL_PRIORITY, false);
	}
	
	private void advance() {
		Direction dir = currentLocation.directionTo(ENEMY_HQ);
		attackTask = new AttackTask(currentLocation.add(dir, STEP_SIZE), PATROL_PRIORITY, false);
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
		
		attackTask = new AttackTask(currentLocation.add(dir, STEP_SIZE), PATROL_PRIORITY, false);
	}
	
	@Override
	public Task getTask() {
		return attackTask;
	}

}
