package team059.movement;

import battlecode.common.*;
import team059.RobotBehavior;
import static team059.utils.Utils.*;

public abstract class NavAlg {
	protected RobotBehavior rb;
	protected RobotController rc;
	protected MapLocation start, finish;
	
	public final int MINE_MOVE_COST = 13;
	public final int NORMAL_MOVE_COST = 1;
	
	public NavAlg(RobotBehavior rb, MapLocation finish) {
		this.rb = rb;
		this.rc = rb.rc;
		this.start = rc.getLocation();
		this.finish = finish;
	}
	
	abstract public void recompute();
	
	abstract public void recompute(MapLocation finish);
	
	/*
	 * Return the next direction to [attempt to] move in.
	 * 
	 * Assume finish is neither (current location) nor null. [Covered in NavSystem switch statement]
	 */
	abstract public Direction getNextDir();
}
