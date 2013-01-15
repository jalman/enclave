package team059.movement;

import team059.RobotBehavior;
import team059.utils.*;
import battlecode.common.*;
import static team059.utils.Utils.*;

public class Mover {
	private RobotBehavior rb;
	private RobotController rc;
	private MapLocation dest, here;
	private boolean defuseMoving;
	private NavAlg navAlg;
	
	public Mover(RobotBehavior rb) { 
		this.rb = rb;
		this.rc = rb.rc;
		this.dest = null;
		this.navAlg = NavType.BUG_DIG_1.navAlg;
		this.defuseMoving = true;
	}

	public void setNavType(NavType navtype) {
		this.navAlg = navtype.navAlg;
	}
	
	public void setTarget(MapLocation dest) {
		if(!dest.equals(this.dest)) {
			this.dest = dest;
			navAlg.recompute(dest);
		}
	}

	public MapLocation getTarget() {
		return dest;
	}
	
	public boolean getDefuseMoving() {
		return defuseMoving;
	}
	
	public void toggleDefuseMoving(boolean b) { 
		defuseMoving = b;
		if(defuseMoving) {
			setNavType(NavType.BUG_DIG_1);
		} else {
			setNavType(NavType.BUG);
		}
	}
	
	public void toggleDefuseMoving() {
		toggleDefuseMoving(!defuseMoving);
	}
	
	public void execute() {
		//int bc = Clock.getBytecodesLeft();
		//rc.setIndicatorString(1, "my x = " + Integer.toString(rc.getLocation().x) + ", my y = " + Integer.toString(rc.getLocation().y)
		//		+ "x = " + Integer.toString(dest.x) + ", y = " + Integer.toString(dest.y)); 
		rc.setIndicatorString(1, dest + "");
		if(rc.isActive()) {
			here = RC.getLocation();
			if(dest == null || dest.equals(here)) {
				return;
			}
			
			Direction d = navAlg.getNextDir();

			if(d != null && d != Direction.NONE && d != Direction.OMNI) {
				moveMine(d);
			}
		}
		//System.out.println("Bytecodes used by Mover.execute() = " + Integer.toString(bc-Clock.getBytecodesLeft()));
	}

	public void moveMine(Direction dir) {
		try {
			MapLocation nextSquare = rc.getLocation().add(dir);
			if(Utils.isEnemyMine(nextSquare)) {
					rc.defuseMine(rc.getLocation().add(dir));
			} if(rc.canMove(dir) && rc.isActive()) {
				rc.move(dir);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

}
