package team059.soldiers;

import team059.movement.Mover;
import team059.movement.NavType;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import static team059.utils.Utils.*;

public class ExpandTask extends TravelTask {

	private static final Mover mover = new Mover();
	
	private final RobotType buildType;
	
	public ExpandTask(MapLocation encampment) {
		this(encampment, strategy.greed, null);
	}

	public ExpandTask(MapLocation encampment, int priority, RobotType buildType) {
		super(mover, encampment, priority, 0);
		this.buildType = buildType;
	}
	
	@Override
	public boolean done() {
		try {
			if(!super.done() && RC.canSenseSquare(destination)) {
				GameObject object = RC.senseObjectAtLocation(destination);
				if(object != null) {
					return true;
				}
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public int appeal() {
		return (int) (super.appeal() - RC.senseCaptureCost() / 5);
	}

	@Override
	public void execute() throws GameActionException {
		if(currentLocation.equals(destination)) {
			if(RC.getTeamPower() >= RC.senseCaptureCost()) {
				RC.captureEncampment(getCaptureType());
			} else {
				//send message to HQ requesting more power!
			}
		} else {
			super.execute();
		}
	}
	
	private RobotType getCaptureType() {
		if(buildType != null) return buildType;
		
		if(forward >= strategy.border - strategy.margin) {
			//TODO: make medbays?
			return RobotType.ARTILLERY;	
		}
		
		//TODO: do something better
		return Clock.getRoundNum() % 2 == 0 ? RobotType.GENERATOR : RobotType.SUPPLIER;
	}

}
