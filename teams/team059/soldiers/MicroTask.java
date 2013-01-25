package team059.soldiers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.utils.Utils;
import static team059.utils.Utils.*;

public class MicroTask extends Task {
	
	@Override
	public boolean done() {
		return enemyRobots.length == 0;
	}
	
	@Override
	public void execute() throws GameActionException {
		SoldierBehavior2.microSystem.run();
	}

	@Override
	public int appeal() {
		return 0;
	}
}
