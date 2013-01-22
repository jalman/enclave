package team059.soldiers;

import team059.Task;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import static team059.utils.Utils.*;

/**
 * Patrol near the frontier.
 * @author vlad
 *
 */
public class PatrolManager extends TaskGiver {
	
	private static final int PATROL_PRIORITY = 10;
	private static final int STEP_SIZE = 4;
	
	private AttackTask attackTask;
	
	@Override
	public void compute() throws GameActionException {
		double position = forward - strategy.border;
		
		if(position > strategy.margin) {
			retreat();
		} else if(position < strategy.margin) {
			advance();
		} else {
			patrol();
		}
	}

	private void retreat() {
		Direction dir = currentLocation.directionTo(ALLY_HQ);
		attackTask = new AttackTask(currentLocation.add(dir, STEP_SIZE), PATROL_PRIORITY);		
	}
	
	private void advance() {
		Direction dir = currentLocation.directionTo(ENEMY_HQ);
		attackTask = new AttackTask(currentLocation.add(dir, STEP_SIZE), PATROL_PRIORITY);				
	}
	
	/**
	 * Move in a random direction.
	 */
	private void patrol() {
		Direction dir = DIRECTIONS[random.nextInt(8)];
		attackTask = new AttackTask(currentLocation.add(dir, STEP_SIZE), PATROL_PRIORITY);
	}
	
	@Override
	public Task getTask() {
		return attackTask;
	}

}
