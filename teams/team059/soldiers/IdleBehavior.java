package team059.soldiers;

import team059.soldiers.SoldierBehavior;
import battlecode.common.*;

public class IdleBehavior extends SoldierBehavior{

	
	public IdleBehavior(RobotController rc) throws GameActionException{
		super(rc);
	}
	
	public void idleBehavior() throws GameActionException {
		mover.defuseMoving = true;		
		//see if there is an encampment nearby to take
		
		if(target == null && rc.getTeamPower() > (1 + rc.senseAlliedEncampmentSquares().length)* 20.0) {
			MapLocation[] encampments = rc.senseEncampmentSquares(rc.getLocation(), 16, Team.NEUTRAL);
			if(encampments.length > 0) {
				int maxdist = 20;
				for(MapLocation encampment : encampments) {
					int dist = encampment.distanceSquaredTo(rc.getLocation());
					if(dist < maxdist) {
						maxdist = dist;
						target = encampment;
					}
				}
			}
		} else if (target != null) { //see if the encampment is taken
			int dist = rc.getLocation().distanceSquaredTo(target);
			if(dist > 0 && dist <= 14) {//change this to deal with upgrades!
				GameObject there = rc.senseObjectAtLocation(target);
				if(there != null) {
					target = null;
				}
			}
		}
		
		//rc.setIndicatorString(0, encampTarget.toString());

		if(rc.senseEncampmentSquare(rc.getLocation()) && rc.senseCaptureCost() < rc.getTeamPower()) {
			try {
				rc.captureEncampment(RobotType.SUPPLIER);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			if(rc.isActive()) {
				mineLayer.randomize();
				if (mineLayer.adjacentToEncampment()&& Math.random() < mineLayer.mineProb*3)
				{
					mineLayer.mineAroundEncampment();
				}
				else{
					if(Math.random() < mineLayer.mineProb*3/4) {
						rc.setIndicatorString(0, "RANDOM MINE");
						rc.layMine();
					} else {
						rc.setIndicatorString(0, mode.name());
						mover.setTarget(target == null ? gather : target);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setIdleTarget() {
		
	}
}
