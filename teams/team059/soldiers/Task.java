package team059.soldiers;

import battlecode.common.GameActionException;

/**
 * A task to be accomplished by the robot.
 * @author vlad
 */
public abstract class Task {
	/**
	 * Updates task-related information.
	 */
	public void update() {}
	
	/**
	 * Evaluates the appeal of this task. Takes into account strategy, messages, round number, etc...
	 * @return An integral estimate of how appealing this task is right now.
	 * Roughly corresponds to the number of rounds of nuclear research this task grants us.
	 */
	public abstract int appeal();
	
	/**
	 * Executes this task for a round.
	 * @throws GameActionException
	 */
	public abstract void execute() throws GameActionException;

	/**
	 * Tests if this task has finished.
	 * @return Whether the task has finished.
	 */
	public abstract boolean done();
}
