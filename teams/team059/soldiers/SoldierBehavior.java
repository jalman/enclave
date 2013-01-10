package team059.soldiers;

import team059.RobotBehavior;
import battlecode.common.*;

public class SoldierBehavior extends RobotBehavior {

	MapLocation encampTarget = null;
	
	
	public SoldierBehavior(RobotController therc) {
		super(therc);
	}

	@Override
	public void run() {
		try {
			if (rc.isActive()) {
				
				//see if there is an encampment nearby to take
				if(encampTarget == null) {
					MapLocation[] encampments = rc.senseEncampmentSquares(rc.getLocation(), 16, Team.NEUTRAL);
					if(encampments.length > 0) {
						int maxdist = 20;
						for(MapLocation encampment : encampments) {
							int dist = encampment.distanceSquaredTo(rc.getLocation());
							if(dist < maxdist) {
								maxdist = dist;
								encampTarget = encampment;
							}
						}
					}
				} else { //see if the encampment is taken
					GameObject there = rc.senseObjectAtLocation(encampTarget);
					if(there != null) {
						encampTarget = null;
					}
				}
				
				rc.setIndicatorString(0, encampTarget + "");
				
				
				if(encampTarget != null && encampTarget.distanceSquaredTo(rc.getLocation()) == 0) {
					try{
						rc.captureEncampment(RobotType.GENERATOR);
					} catch(Exception e) {
						e.printStackTrace();
					}
				} else {
					
					Direction dir = encampTarget == null ? rc.getLocation().directionTo(rc.senseEnemyHQLocation()) : rc.getLocation().directionTo(encampTarget);
					
					if(!moveMine(dir)) {
						if(!moveMine(dir.rotateLeft()))
							moveMine(dir.rotateRight());
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean moveMine(Direction dir) {
		Team mine = rc.senseMine(rc.getLocation().add(dir));
		if(mine != null && mine != rc.getTeam()) {
			try {
				rc.defuseMine(rc.getLocation().add(dir));
				return true;
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
		
		if(rc.canMove(dir)) {
			try {
				rc.move(dir);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	public void buildEncampment(Direction dir) {

	}

	public void researchUpgrade(Upgrade upg) {

	}
}
