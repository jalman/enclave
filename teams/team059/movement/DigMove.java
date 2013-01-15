package team059.movement;

import battlecode.common.*;
import team059.RobotBehavior;
import static team059.utils.Utils.*;

public class DigMove extends NavAlg {
	
	public void recompute() { }
	
	public void recompute(MapLocation finish) {
		this.finish = finish;
	}
	
	public Direction getNextDir() {
		/*
		if(finish == null || finish == RC.getLocation()) {
			return Direction.NONE;
		}*/
		return RC.getLocation().directionTo(finish);
	}

}
