package team059.movement;

import battlecode.common.*;
import team059.RobotBehavior;

public class NavSystem {
	private RobotController rc;
	private RobotBehavior rb;
	public NavType navtype;
	public MapLocation myLoc;
	public MapLocation currentDest;
	public final AStar1 astar1;
	
	public NavSystem(RobotBehavior rb) { 
		this.rb = rb;
		this.rc = rb.rc;
		this.navtype = NavType.ASTAR1;
		this.myLoc = rc.getLocation();
		this.astar1 = new AStar1(rb, null);
		this.currentDest = null;
	}
	
	public Direction navToward(MapLocation dest) {
		int bc = Clock.getBytecodesLeft();
		myLoc = rc.getLocation();
		if(dest == null || myLoc.equals(dest)) {
			return Direction.NONE;
		}
		Direction d = navTowardLongRange(dest); // just for now
		System.out.println("Direction: " + d.toString() + ". Bytecodes used by navToward = " + Integer.toString(bc-Clock.getBytecodesLeft()));
		return d;
	}
	
	public Direction navTowardLongRange(MapLocation dest) {
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
}
