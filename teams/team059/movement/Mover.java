package team059.movement;

import battlecode.common.*;
import team059.RobotBehavior;
import team059.utils.*;

public class Mover {
	private RobotBehavior rb;
	private RobotController rc;
	private Utils ut;
	public MapLocation dest;
	public final NavSystem navsys;
	
	public Mover(RobotBehavior rb) { 
		this.rb = rb;
		this.rc = rb.rc;
		this.dest = null;
		this.navsys = new NavSystem(rb);
	}
	
	public void setTarget(MapLocation dest) {
		this.dest = dest;
	}

	public MapLocation getTarget() {
		return dest;
	}
	
	public void execute() {
		//int bc = Clock.getBytecodesLeft();
		//rc.setIndicatorString(1, "my x = " + Integer.toString(rc.getLocation().x) + ", my y = " + Integer.toString(rc.getLocation().y)
		//		+ "x = " + Integer.toString(dest.x) + ", y = " + Integer.toString(dest.y)); 
		rc.setIndicatorString(1, dest + "");
		if(rc.isActive() && dest != null) {
			Direction d = navsys.navToward(dest);
			if(d != null) {
				//System.out.println("d = " + d.toString());
				moveMine(d);
			}
		}
		//System.out.println("Bytecodes used by Mover.execute() = " + Integer.toString(bc-Clock.getBytecodesLeft()));
	}

	public void moveMine(Direction dir) {
		try {
			if(dir == Direction.NONE || dir == Direction.OMNI) {
				return;
			}
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
	
	public void aboutMoveMine(Direction dir) {
		try {
			if(!rc.isActive() || dir == Direction.NONE || dir == Direction.OMNI) {
				return;
			}
			if(empty(dir)) {
				rc.move(dir);
			} else if (empty(dir.rotateLeft())) {
				rc.move(dir.rotateLeft())
			} else if (empty(dir.rotateRight())) {
				rc.move(dir.rotateRight())
			} else {
				moveMine(dir);
			}

		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	private boolean empty(Direction dir) {
		MapLocation nextSquare = rc.getLocation().add(dir);
		if(Utils.isEnemyMine(nextSquare) || !rc.canMove(dir)) {
			return false;
		}
		return true;
	}

}
