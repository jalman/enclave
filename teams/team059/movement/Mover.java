package team059.movement;

import battlecode.common.*;
import team059_test_damien.utils.*;
//import team059_test_damien

public class Mover {
	private RobotController rc;
	private Utils ut;
	public MapLocation dest;
	public final NavSystem navsys;
	
	public Mover(RobotController rc) { 
		this.rc = rc;
		this.ut = new Utils(rc);
		this.dest = null;
		this.navsys = new NavSystem(rc);
	}
	
	public void setTarget(MapLocation dest) {
		this.dest = dest;
	}

	public MapLocation getTarget() {
		return dest;
	}
	
	public void execute() {
		//int bc = Clock.getBytecodesLeft();
		rc.setIndicatorString(1, "my x = " + Integer.toString(rc.getLocation().x) + ", my y = " + Integer.toString(rc.getLocation().y)
				+ "x = " + Integer.toString(dest.x) + ", y = " + Integer.toString(dest.y)); 
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
			if(ut.isEnemyMine(nextSquare)) {
					rc.defuseMine(rc.getLocation().add(dir));
			} if(rc.canMove(dir) && rc.isActive()) {
				rc.move(dir);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
}
