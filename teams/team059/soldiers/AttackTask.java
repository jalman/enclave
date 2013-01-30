package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.movement.Mover;
import static team059.soldiers.SoldierUtils.*;
import static team059.utils.Utils.*;

public class AttackTask extends TravelTask {

	private static final Mover mover = new Mover();
	
	private final boolean defuse;
	private final int timidity;
	
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
		
		if(farawayEnemyTarget != null) {
			SoldierBehavior2.microSystem.run(timidity);
		} else {
			//if(!(defuse && Mines.tryDefuse(destination, false))) {
				super.execute();
			//}
		}
//		RC.setIndicatorString(2, "Away turns " + turnsMicroedForAwayFromDestination + " back turns " + turnsSpentGoingBack + " Faraway Target " + farawayEnemyTarget + " 	Destination " + destination + " Round " + Clock.getRoundNum());
	}

	@Override
	public String toString() {
		return "ATTACKING TOWARD " + destination;
	}
}
