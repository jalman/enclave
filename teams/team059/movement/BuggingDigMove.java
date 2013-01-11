package team059.movement;

import battlecode.common.*;
import team059.RobotBehavior;
import static team059.utils.Utils.*;

public class BuggingDigMove extends NavAlg {
	
	//public Direction lastDir;
	private Direction tryDir, refDir;
	private boolean bugging, hugLeft, waiting;
	
	public BuggingDigMove(RobotBehavior rb, MapLocation finish) {
		super(rb, finish);
		//lastDir = Direction.NONE;
	}
	
	public void recompute() {
	}
	
	public void recompute(MapLocation finish) {
		this.finish = finish;
	}
	
	/*public void recompute(MapLocation finish, Direction lastDir) {
		this.lastDir = lastDir;
		this.finish = finish;
	}
	*/
	
	public Direction getNextDir() {
		this.start = RC.getLocation();
		
		tryDir = start.directionTo(finish);
		
		if(!bugging) {
			if(RC.canMove(tryDir)) {
				return tryDir;
			} else {
				bugging = true;
				hugLeft = ( naiveDistance(start.add(tryDir.rotateRight()), finish) 
								< naiveDistance(start.add(tryDir.rotateLeft()), finish) ? true : false );
			}
		}
		
		// if bugging...
		refDir = tryDir;
		if(hugLeft) {
			if(RC.canMove(tryDir)) {
				bugging = false;
				return tryDir;
			} else {
				do {
					tryDir = tryDir.rotateRight();
				} while(!RC.canMove(tryDir));
				return (refDir.equals(tryDir) ? Direction.NONE : tryDir ) ;
			}
		} else {
			if(RC.canMove(tryDir)) {
				bugging = false;
				return tryDir;
			} else {
				do {
					tryDir = tryDir.rotateLeft();
				} while(!RC.canMove(tryDir));
				return (refDir.equals(tryDir) ? Direction.NONE : tryDir ) ;
			}
		}
	}
}
