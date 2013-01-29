package team059.soldiers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.movement.Mover;
import static team059.soldiers.SoldierUtils.*;
import static team059.utils.Utils.*;

public class AttackTask extends TravelTask {

	private static final Mover mover = new Mover();
	
	private final boolean defuse;
	private final int timidity;
	private int turnsMicroedForAwayFromDestination;
	final int turnsIShouldMicroFor = 25;
	private final int farawayThreshold = 8;
	private int turnsSpentGoingBack = 0;
	final int turnsToWaitUntilMicroIsAllowedAgain = 13;
	
	public AttackTask(MapLocation target, int priority) {
		this(target, priority, parameters.timidity, false);
	}	
	
	public AttackTask(MapLocation target, int priority, boolean defuse) {
		this(target, priority, parameters.timidity, defuse);
	}

	public AttackTask(MapLocation target, int priority, int timidity, boolean defuse) {
		super(mover, target, priority, 2);
		this.timidity = timidity;
		this.defuse = defuse;
	}
	
	@Override
	public boolean done() {
		if(enemyRobots.length > 0) return false;
		return super.done();
	}

	@Override
	public void execute() throws GameActionException {
		if(Mines.tryDefuse(destination, true)) return;
		if(farawayEnemyTarget != null && turnsMicroedForAwayFromDestination < turnsIShouldMicroFor && turnsMicroedForAwayFromDestination >= 0) {
				runMicro();
		} else {
			returnToPreMicroLocation();
			if(!(defuse && Mines.tryDefuse(destination, false))) {
				super.execute();
			}
		}
	}

	@Override
	public String toString() {
		return "ATTACKING TOWARD " + destination;
	}
	
	private void runMicro() throws GameActionException{
		SoldierBehavior2.microSystem.run(timidity);
		turnsSpentGoingBack = 0;
		if (naiveDistance(currentLocation, destination) > farawayThreshold)
			turnsMicroedForAwayFromDestination++;
	}
	private void returnToPreMicroLocation(){
		if (turnsMicroedForAwayFromDestination >= turnsIShouldMicroFor)
		{
			turnsMicroedForAwayFromDestination = -1;
		}
		else if (naiveDistance(currentLocation, destination) <= farawayThreshold || turnsSpentGoingBack > turnsToWaitUntilMicroIsAllowedAgain)
			turnsMicroedForAwayFromDestination = 0;
		turnsSpentGoingBack ++;
	}
}
