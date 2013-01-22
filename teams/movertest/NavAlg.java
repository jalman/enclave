package movertest;

import battlecode.common.*;
import static movertest.Utils.*;

public abstract class NavAlg {
	protected MapLocation curLoc = null, finish = null;
	
	public static final int MINE_MOVE_COST = 13;
	public static final int NORMAL_MOVE_COST = 1;
	
	abstract public void recompute();
	
	abstract public void recompute(MapLocation finish);
	
	/*
	 * Return the next direction to [attempt to] move in.
	 * 
	 * Assume finish is neither (current location) nor null. [Covered in NavSystem switch statement]
	 */
	abstract public Direction getNextDir();
}
