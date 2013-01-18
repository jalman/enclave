package simplebot.movement;

import simplebot.RobotBehavior;
import battlecode.common.*;
import static simplebot.utils.Utils.*;

public class DigMove extends NavAlg {
	
	public void recompute() { }
	
	public void recompute(MapLocation finish) {
		this.finish = finish;
	}
	
	public Direction getNextDir() {
		
		if(finish == null || finish == RC.getLocation()) {
			return Direction.NONE;
		}
		//System.out.println(finish);
		return RC.getLocation().directionTo(finish);
	}

}
