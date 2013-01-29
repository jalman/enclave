package team059.soldiers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.movement.Mover;
import static team059.utils.Utils.*;

public class TravelTask extends Task {

	public static final Mover defaultMover = new Mover();
	
	public final Mover mover;
	public final MapLocation destination;
	public final int priority;
	/**
	 * How close we want to get to the target.
	 */
	public final int distance;
	/**
	 * A measure of how willing we are to engage the enemy.
	 */
	public final int attack = 0;
	
	protected int eta;
	
	public TravelTask(Mover mover, MapLocation destination, int priority, int distance) {
		this.mover = mover;
		this.destination = destination;
		this.priority = priority;
		this.distance = distance;
	}

	public TravelTask(MapLocation destination, int priority, int distance) {
		this.mover = defaultMover;
		this.destination = destination;
		this.priority = priority;
		this.distance = distance;
	}
	
	@Override
	public void update() {
		eta = naiveDistance(currentLocation, destination) - distance;		
	}
	
	@Override
	public int appeal() {
		return priority - eta;
	}

	@Override
	public void execute() throws GameActionException {
		if(RC.canMove(currentLocation.directionTo(destination)) || !Mines.tryDefuse(destination, false)) {
			mover.setTarget(destination);
			mover.execute();
		}
	}
	
	@Override
	public boolean done() {
		return eta <= 0;
	}
	
	@Override
	public String toString() {
		return "Move to " + destination;
	}

}
