package team059_presprint.movement;

import battlecode.common.*;
import team059_presprint.RobotBehavior;
import static team059_presprint.utils.Utils.*;

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
