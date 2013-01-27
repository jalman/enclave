package team059.movement;

import battlecode.common.*;
import static team059.utils.Utils.*;

public class BuggingDigMove extends NavAlg {
	
	//public Direction lastDir;
	private Direction tryDir, refDir;
	private boolean bugging, hugLeft;
	
	public BuggingDigMove() {
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
		this.curLoc = RC.getLocation();
		
		if(finish == null) return Direction.NONE;
		
		tryDir = curLoc.directionTo(finish);
		
		if(tryDir == Direction.NONE || tryDir == Direction.OMNI) return Direction.NONE;
		
		if(!bugging) {
			if(canMoveNoMine(tryDir)) {
				return tryDir;
			} else if(canMoveNoMine(tryDir.rotateLeft())) {
				return tryDir.rotateLeft();
			} else if(canMoveNoMine(tryDir.rotateRight())) {
				return tryDir.rotateRight();
			} else {
				bugging = true;
				hugLeft = ( naiveDistance(curLoc.add(tryDir.rotateRight()), finish) 
								< naiveDistance(curLoc.add(tryDir.rotateLeft()), finish) ? true : false );
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

	private boolean canMoveNoMine(Direction d) {
		//System.out.println(d);
		if(d==null) return true;
		return ( RC.canMove(d) && !isEnemyMine(curLoc.add(d)) );
	}
}
