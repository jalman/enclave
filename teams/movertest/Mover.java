package movertest;

import battlecode.common.*;
import static movertest.Utils.*;

public class Mover {
	private MapLocation dest, here;
	private boolean defuseMoving;
	private NavAlg navAlg;
	
	public Mover() { 
		this.dest = null;
		this.navAlg = NavType.BUG_OLD.navAlg;
		this.defuseMoving = true;
	}
	
	public Mover(NavType navType) {
		this.dest = null;
		this.navAlg = navType.navAlg;
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
	
//	public boolean getDefuseMoving() {
//		return defuseMoving;
//	}
//	
//	public void toggleDefuseMoving(boolean b) { 
//		defuseMoving = b;
//		if(defuseMoving) {
//			setNavType(NavType.BUG_DIG_2);
//		} else {
//			setNavType(NavType.BUG);
//		}
//	}
//	
//	public void toggleDefuseMoving() {
//		toggleDefuseMoving(!defuseMoving);
//	}

	public void execute(boolean canMoveMine) {
		//RC.setIndicatorString(1, dest + "");
		if(RC.isActive()) {
			here = RC.getLocation();
			if(dest == null || dest.equals(here)) {
				return;
			}
			
			Direction d = navAlg.getNextDir();			
			
			if(d != null && d != Direction.NONE && d != Direction.OMNI) {
				if(canMoveMine) {
					try {
					if(RC.canMove(d)) {
						RC.move(d);
					}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else {
					moveMine(d);
				}
			}
			
		}
	}
	
	public void execute() {
		//int bc = Clock.getBytecodesLeft();
		//RC.setIndicatorString(1, "my x = " + Integer.toString(RC.getLocation().x) + ", my y = " + Integer.toString(RC.getLocation().y)
		//		+ "x = " + Integer.toString(dest.x) + ", y = " + Integer.toString(dest.y)); 
		if(RC.isActive()) {
			here = RC.getLocation();
			if(dest == null || dest.equals(here)) {
				return;
			}
			
			Direction d = navAlg.getNextDir();
			RC.setIndicatorString(1,  Clock.getRoundNum() + " " + dest + " " + d);

			if(d != null && d != Direction.NONE && d != Direction.OMNI) {
				moveMine(d);
			}
		}
		//System.out.println("Bytecodes used by Mover.execute() = " + Integer.toString(bc-Clock.getBytecodesLeft()));
	}

	public void moveMine(Direction dir) {
		try {
			MapLocation nextSquare = RC.getLocation().add(dir);
			if(Utils.isEnemyMine(nextSquare)) {
					RC.defuseMine(RC.getLocation().add(dir));
			} else if(RC.canMove(dir) && RC.isActive()) {
				RC.move(dir);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

}
