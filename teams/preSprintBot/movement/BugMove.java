package preSprintBot.movement;

import preSprintBot.RobotBehavior;
import battlecode.common.*;
import static preSprintBot.utils.Utils.*;

public class BugMove extends NavAlg {
	
	public Direction lastDir;
	private Direction wantDir, tryDir;
	private boolean bugging, hugLeft, waiting;
	private MapLocation beganBugging;
	
	public BugMove() {
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
		Direction d = getNextDirPrivate();
		RC.setIndicatorString(2, "Bugging: " + bugging + ", hugLeft: " + hugLeft + ", direction: " + d);
		lastDir = d;
		return d;
	}
		
	private Direction getNextDirPrivate() {
		this.start = RC.getLocation();
		
		wantDir = start.directionTo(finish);
		
		if(!bugging) {
			if(canMoveNoMine(wantDir)) {
				return wantDir;
			} else {
				bugging = true;
				beganBugging = RC.getLocation();
				hugLeft = ( naiveDistance(start.add(wantDir.rotateRight()), finish) 
								< naiveDistance(start.add(wantDir.rotateLeft()), finish) ? true : false );
			}
		}
		
		// if bugging...

		if(canMoveNoMine(wantDir) && start.distanceSquaredTo(finish) < beganBugging.distanceSquaredTo(finish)) {
			bugging = false;
			return wantDir;
		} 
		
		if(hugLeft) {
			tryDir = lastDir.rotateRight().rotateRight();
			for(int i=0; i<8; i++) {
				tryDir = tryDir.rotateLeft();
				if(canMoveNoMine(tryDir) && !lastDir.equals(tryDir.opposite())) {
					return tryDir;
				}
			}
		} else {
			tryDir = lastDir.rotateLeft().rotateLeft();
			for(int i=0; i<8; i++) {
				tryDir = tryDir.rotateRight();
				if(canMoveNoMine(tryDir) && !lastDir.equals(tryDir.opposite())) {
					return tryDir;
				}
			}
		}
		return Direction.NONE;

	}
	
	private boolean canMoveNoMine(Direction d) {
		//System.out.println(d);
		if(d==null) return true;
		return ( RC.canMove(d) && !isEnemyMine(start.add(d)) );
	}
}
