package artillerybot1.soldiers;

import artillerybot1.RobotBehavior;
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
				if(encampTarget == null && rc.getTeamPower() > rc.senseCaptureCost()) {
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
				} else if (encampTarget != null) { //see if the encampment is taken
					int dist = rc.getLocation().distanceSquaredTo(encampTarget);
					if(dist > 0 && dist <= 14) {//change this to deal with upgrades!
						GameObject there = rc.senseObjectAtLocation(encampTarget);
						if(there != null) {
							encampTarget = null;
						}
					}
				}
				
				rc.setIndicatorString(0, encampTarget + "");
				
				
				if(encampTarget != null && rc.getTeamPower() > rc.senseCaptureCost() && encampTarget.distanceSquaredTo(rc.getLocation()) == 0) {
					try{
						rc.captureEncampment(RobotType.ARTILLERY);
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
		if(dir == null || dir == Direction.NONE || dir == Direction.OMNI) return false;
		
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
