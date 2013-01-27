package movertest;

import battlecode.common.*;
import static movertest.Utils.*;

public class Mover {
	private MapLocation dest, here;
//	private boolean defuseMoving;
	private NavAlg navAlg;

	public Mover() { 
		this.dest = null;
		this.navAlg = NavType.BUG_DIG_2.navAlg;
//		this.defuseMoving = true;
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

//	public boolean getDefuseMoving() {
//		return defuseMoving;
//	}
//
//	public void toggleDefuseMoving(boolean b) { 
//		defuseMoving = b;
//		if(defuseMoving) {
//			setNavType(NavType.BUG);
//		} else {
//			//setNavType(NavType.BUG);
//		}
//	}
//
//	public void toggleDefuseMoving() {
//		toggleDefuseMoving(!defuseMoving);
//	}

	public void execute() {
		RC.setIndicatorString(1, dest + "");
		if(RC.isActive()) {
			here = RC.getLocation();
			if(dest == null || dest.equals(here)) {
				return;
			}

			navAlg.recompute();
			Direction d = navAlg.getNextDir();
			
			if(d != null && d != Direction.NONE && d != Direction.OMNI) {
				moveMine(d);
			}
		}
	}

	public void moveMine(Direction dir) {
		try {
			MapLocation nextSquare = RC.getLocation().add(dir);
			if(Utils.isEnemyMine(nextSquare)) {
				RC.defuseMine(nextSquare);
			} else if(RC.canMove(dir)) {
				RC.move(dir);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

}
