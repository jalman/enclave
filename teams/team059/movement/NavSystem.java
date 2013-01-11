package team059.movement;

import battlecode.common.*;
import team059.RobotBehavior;
import team059.movement.*;

public class NavSystem {
	private RobotController rc;
	private RobotBehavior rb;
	public NavType navtype;
	public MapLocation myLoc;
	public MapLocation currentDest;
	public final AStar1 astar1;
	public final AStar2 astar2;
	public final DigMove digmove;
	public final BuggingDigMove bugging_digmove;
	//public Direction lastDirectionMoved;
	
	public NavSystem(RobotBehavior rb) { 
		this.rb = rb;
		this.rc = rb.rc;
		this.navtype = NavType.BUG_STRAIGHT_DIG;
		this.myLoc = rc.getLocation();
		this.digmove = new DigMove(rb, null);
		this.bugging_digmove = new BuggingDigMove(rb, null);
		this.astar1 = new AStar1(rb, null);
		this.astar2 = new AStar2(null);
		this.currentDest = null;
		//this.lastDirectionMoved = Direction.NONE;
	}
	
	public void changeNavType(NavType navtype) {
		this.navtype = navtype;
	}
	
	public Direction navToward(MapLocation dest) {
		//int bc = Clock.getBytecodesLeft();
		myLoc = rc.getLocation();
		
		if(dest == null || myLoc.equals(dest)) {
			return Direction.NONE;
		}
		
		switch(navtype) {
		case ASTAR1:
			return navTowardAStar1(dest);
		case ASTAR2:
			return navTowardAStar2(dest);
		case STRAIGHT_DIG:
			return navTowardDig(dest);
		case BUG_STRAIGHT_DIG:
			return navTowardBugStraightDig(dest);
		default:
			return Direction.NONE;
		}
		/*
		if(dest == null || myLoc.equals(dest)) {
			return Direction.NONE;
		}*/
		// Direction d = navTowardLongRange2(dest); // just for now
		//System.out.println("Direction: " + d.toString() + ". Bytecodes used by navToward = " + Integer.toString(bc-Clock.getBytecodesLeft()));
	}
	
	public Direction navTowardDig(MapLocation dest) {
		digmove.recompute(dest);
		return digmove.getNextDir();
	}
	
	public Direction navTowardBugStraightDig(MapLocation dest) {
		bugging_digmove.recompute(dest);
		return bugging_digmove.getNextDir();
	}
	
	public Direction navTowardAStar1(MapLocation dest) {
		if(dest.equals(currentDest)) {
			Direction d = astar1.getNextDir();
			//System.out.println("RECOMPUTED0 astar1 returns direction " + d.toString());
			if(d == Direction.OMNI || d == Direction.NONE || !rc.canMove(d)) {
				System.out.println("RECOMPUTED1 astar1 returns direction " + d.toString());
				astar1.recompute();
				d = astar1.getNextDir();	// hopefully the new d is valid.....
			}
			return d;
		} else {
			currentDest = dest;
			astar1.recompute(dest);
			return astar1.getNextDir();
		}
	}

	public Direction navTowardAStar2(MapLocation dest) {
		if(dest.equals(currentDest)) {
			Direction d = astar2.getNextDir();
			//System.out.println("RECOMPUTED0 astar1 returns direction " + d.toString());
			if(d == Direction.OMNI || d == Direction.NONE || !rc.canMove(d)) {
				System.out.println("RECOMPUTED1 astar1 returns direction " + d.toString());
				astar2.recompute();
				d = astar2.getNextDir();
			}
			return d;
		} else {
			currentDest = dest;
			astar2.recompute(dest);
			return astar2.getNextDir();
		}
	}
}
