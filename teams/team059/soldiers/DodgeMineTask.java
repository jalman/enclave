package team059.soldiers;

import static team059.utils.Utils.*;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

/**
 * Step off a mine.
 * @author vlad
 *
 */
public class DodgeMineTask extends Task {

	public static final double MIN_SHIELDS = 40;
	
	boolean onEnemyMine;
	
	@Override
	public void update() {
		onEnemyMine = isEnemyMine(currentMine);
	}
	
	@Override
	public int appeal() {
		if(onEnemyMine && RC.getShields() < MIN_SHIELDS) {
			return Integer.MAX_VALUE;
		} else {
			return Integer.MIN_VALUE;
		}
	}

	@Override
	public boolean done() {
		return onEnemyMine;
	}

	@Override
	public void execute() throws GameActionException {
		Direction dir = currentLocation.directionTo(ALLY_HQ);
		for(int i = 0; i < 8; i++) {
			MapLocation loc = currentLocation.add(dir);
			
			if(!isEnemyMine(loc) && RC.canMove(dir)) {
//				System.out.println("Dodged mine!");
				RC.move(dir);
				break;
			}
			
			dir = dir.rotateRight();
		}
	}
}
