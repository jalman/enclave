package team059.movement;

import battlecode.common.*;
import static team059.utils.Utils.*;

public class BugMove extends NavAlg {
	
	public Direction lastDir;
	private Direction wantDir, tryDir, refDir;
	private boolean bugging, hugLeft, waiting;
	private MapLocation beganBugging;

	//StringBuilder s; // = new StringBuilder();
	
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
		//RC.setIndicatorString(2, "Bugging: " + bugging + ", hugLeft: " + hugLeft + ", direction: " + d);
		lastDir = d;
//		s.append( " | " + d);
//		RC.setIndicatorString(0, s.toString());
		return d;
	}
		
	private Direction getNextDirPrivate() {

		if(finish == null) return Direction.NONE;
		
		tryDir = currentLocation.directionTo(finish);
		
		if(tryDir == Direction.NONE || tryDir == Direction.OMNI) return Direction.NONE;
		
		if(currentLocation.isAdjacentTo(finish)) {
			return (canMoveNoMine(tryDir)) ? currentLocation.directionTo(finish) : Direction.NONE;
		}
		
		
//		if(!bugging) {
//			if(canMoveNoMine(wantDir)) {
//				return wantDir;
//			} else {
//				bugging = true;
//				beganBugging = RC.getLocation();
//				hugLeft = ( naiveDistance(currentLocation.add(wantDir.rotateRight()), finish) 
//								< naiveDistance(currentLocation.add(wantDir.rotateLeft()), finish) ? true : false );
//			}
//		}
//		s = new StringBuilder();
//		s.append(Clock.getRoundNum());
		if(!bugging) {
//			s.append(" | not bugging");
			if(canMoveNoMine(tryDir)) {
//				s.append(" | canMoveNoMine(tryDir)");
				return tryDir;
			} else {
				bugging = true;
				hugLeft = ( naiveDistance(currentLocation.add(tryDir.rotateRight()), finish) 
								< naiveDistance(currentLocation.add(tryDir.rotateLeft()), finish) ? true : false );
			}
		}
		
		// if bugging...
//		s.append(" | bugging");
		refDir = tryDir;
		if(hugLeft) {
			if(canMoveNoMine(tryDir)) {
//				s.append(" | canMoveNoMine(tryDir)");
				bugging = false;
				return tryDir;
			} else {
				do {
					tryDir = tryDir.rotateRight();
				} while(!canMoveNoMine(tryDir));
				return (refDir.equals(tryDir) ? Direction.NONE : tryDir ) ;
			}
		} else {
			if(canMoveNoMine(tryDir)) {
//				s.append(" | canMoveNoMine(tryDir)");
				bugging = false;
				return tryDir;
			} else {
				do {
					tryDir = tryDir.rotateLeft();
				} while(!canMoveNoMine(tryDir));
				return (refDir.equals(tryDir) ? Direction.NONE : tryDir ) ;
			}
		}
		
//		// if bugging...
//
//		if(canMoveNoMine(wantDir) && currentLocation.distanceSquaredTo(finish) < beganBugging.distanceSquaredTo(finish)) {
//			bugging = false;
//			return wantDir;
//		} 
//		
//		if(hugLeft) {
//			tryDir = lastDir.rotateRight().rotateRight();
//			for(int i=0; i<8; i++) {
//				tryDir = tryDir.rotateLeft();
//				if(canMoveNoMine(tryDir) && !lastDir.equals(tryDir.opposite())) {
//					return tryDir;
//				}
//			}
//		} else {
//			tryDir = lastDir.rotateLeft().rotateLeft();
//			for(int i=0; i<8; i++) {
//				tryDir = tryDir.rotateRight();
//				if(canMoveNoMine(tryDir) && !lastDir.equals(tryDir.opposite())) {
//					return tryDir;
//				}
//			}
//		}
//		return Direction.NONE;

	}
	
	private boolean canMoveNoMine(Direction d) {
		//System.out.println(d);
		if(d==null) return true;
		return ( RC.canMove(d) && !isEnemyMine(currentLocation.add(d)) );
	}
}
