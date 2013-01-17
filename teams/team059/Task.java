package team059;

import battlecode.common.GameActionException;

/**
 * A task to be accomplished by the robot.
 * @author vlad
 */
public interface Task {
	/**
	 * Evaluates the appeal of this task. Takes into account strategy, messages, round number, etc...
	 * @return An integral estimate of how appealing this task is right now.
	 */
	public int appeal();
	
	/**
	 * Executes this task for a round.
	 * @return Whether the task has finished.
	 * @throws GameActionException
	 */
	public boolean execute() throws GameActionException;
}
