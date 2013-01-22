package team059.soldiers;

import battlecode.common.GameActionException;
import team059.Task;

public abstract class TaskGiver {
	public abstract void compute() throws GameActionException;
	public abstract Task getTask();
}
