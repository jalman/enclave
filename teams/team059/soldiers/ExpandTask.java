package team059.soldiers;

import team059.Encampments;
import team059.Strategy;
import team059.utils.Shields;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Team;
import static team059.utils.Utils.*;

public class ExpandTask extends TravelTask {
	
	private final RobotType buildType;
	private final MapLocation badA, badB;
	private boolean init = true;
	
	public ExpandTask(MapLocation encampment) {
		this(encampment, parameters.greed, null);
	}

	public ExpandTask(MapLocation encampment, int priority, RobotType buildType) {
		super(encampment, priority, 0);
		this.buildType = buildType;
		
		Direction dirToEnemy = ALLY_HQ.directionTo(ENEMY_HQ);
		badA = ALLY_HQ.add(dirToEnemy);
		badB = badA.add(dirToEnemy);
	}
	
	@Override
	public boolean done() {
		
		try {
			if(!currentLocation.equals(destination) && RC.canSenseSquare(destination)) {
				GameObject object = RC.senseObjectAtLocation(destination);
				if(object != null) {
					return true;
				}
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}

		if(Encampments.value[destination.x][destination.y] > Clock.getRoundNum() + appeal()) {
			return true;
		}
		
		return false;
	}

	@Override
	public int appeal() {
		return destination.equals(badA) || destination.equals(badB) ? -10000 : (int) (super.appeal() - RC.senseCaptureCost() / 5);
	}

	@Override
	public void execute() throws GameActionException {
		if(init) {
			messagingSystem.writeTakingEncampmentMessage(destination, appeal());
			init = false;
		}
		
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
		
		if(strategy == Strategy.NORMAL && isBorder(currentLocation) && Shields.shieldLocations.isEmpty()) {
			try {
				messagingSystem.writeShieldLocationMessage(currentLocation);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			return RobotType.SHIELDS;
		}
		
		if(!isSafe(currentLocation)) {
				return RobotType.ARTILLERY;
		}
		
		if(buildType != null) return buildType;
		
		//TODO: do something better
		return RC.senseAlliedEncampmentSquares().length > 8 && Clock.getRoundNum() % 4 != 0 ? RobotType.GENERATOR : RobotType.SUPPLIER;
	}
	
	@Override
	public String toString() {
		return "EXPAND " + buildType + " AT " + destination;
	}

}
