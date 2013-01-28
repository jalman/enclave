package team059.soldiers;

import battlecode.common.GameActionException;

/**
 * A task to be accomplished by the robot.
 * @author vlad
 */
public abstract class Task {
	/**
	 * Updates task-related information. Must be called before any other methods this round.
	 */
	public void update() {}
	
	/**
	 * Evaluates the appeal of this task. Takes into account strategy, messages, round number, etc...
	 * @return An integral estimate of how appealing this task is right now.
	 * Roughly corresponds to the number of rounds of nuclear research this task grants us.
	 */
	public abstract int appeal();

	/**
	 * Tests if this task has finished. Will be called before execute.
	 * @return Whether the task has finished.
	 */
	public abstract boolean done();
	
	/**
	 * Executes this task for a round.
	 * @throws GameActionException
	 */
	public abstract void execute() throws GameActionException;
}
