package team059.soldiers;

import battlecode.common.GameActionException;

public abstract class TaskGiver {
	public abstract void compute() throws GameActionException;
	public abstract Task getTask();
}
