package team059.soldiers;

import static team059.utils.Utils.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;

public class DefuseTask extends TravelTask {

	private boolean init = true;
	private boolean inRange;
	
	public DefuseTask(MapLocation destination, int priority) {
		super(destination, priority, 1);
	}

	@Override
	public void update() {
		inRange = currentLocation.distanceSquaredTo(destination) <= mineRange2;
	}
	
	@Override
	public boolean done() {
		return isEnemyMine(destination) || Mines.defuse[destination.x][destination.y] > Clock.getRoundNum() - GameConstants.MINE_DEFUSE_DELAY;
	}
	
	@Override
	public void execute() throws GameActionException {
		if(init) {
			messagingSystem.writeDefusingMineMessage(destination, appeal());
			init = false;
		}
		
		if(inRange) {
			RC.defuseMine(destination);
		} else {
			super.execute();
		}
	}
	
	@Override
	public String toString() {
		return "Defusing mine at " + destination;
	}
	
}
