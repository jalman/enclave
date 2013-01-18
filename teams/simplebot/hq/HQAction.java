package simplebot.hq;

import battlecode.common.GameActionException;

public interface HQAction {
	/**
	 * Executes this action for a round.
	 * @return If this action has finished executing.
	 */
	public boolean execute(HQBehavior hq) throws GameActionException;
}
