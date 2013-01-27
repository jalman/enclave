package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team059.RobotBehavior;
import team059.movement.Mover;
import team059.soldiers.micro.Micro;
import static team059.utils.Utils.*;

public class AntinukeSoldierBehavior extends RobotBehavior {

//	0-9: encamp
//	10-34: DIFFUSION
//	35,45,55,65,75,85,95,105: ATTACKER group 1
//	115,125,135,145,155: Diffusers
//	after: ATTACKER unless need more diffusers
	
	
	
	
	/* TODOS
	 * - make different kinds of movement
	 *   - defuse move
	 *   - don't care about mines move
	 * -micro??
	 * 
	 */
	
	enum Role{
		ENCAMPMENT, //going to make the shield generator
		DEFUSER, //going to defuse enemy mines
		ATTACKER, //going to charge sheilds at the sheild generator
		CHARGING, //is next to shield and charging
		KILLER //has shields and is going in!
	}
	
	private Role role;

	Mover mover = new Mover();
	Micro microSystem = new Micro();
	
	MapLocation shield = null;
	
	
	Direction attackerDirection = shield.directionTo(ALLY_HQ);
	MapLocation target = shield.add(attackerDirection);
	
	public AntinukeSoldierBehavior() {
		if(Clock.getRoundNum() < 3) {
			role = Role.ENCAMPMENT;
		} else if (Clock.getRoundNum() < 108) {
			initAttacker();
		} else if (Clock.getRoundNum() < 158) {
			initDefuser();
		} else {
			role = Role.ATTACKER;
		}
	}
	
	private void initDefuser() {
		role = Role.DEFUSER;
		mover.setTarget(ENEMY_HQ);
	}
	
	private void initAttacker() {
		role = Role.DEFUSER;
		mover.setTarget(shield.add(attackerDirection));
	}

	
	@Override
	public void run() throws GameActionException {
		switch (role) {
		case ENCAMPMENT:
			runEncampment();
			break;
		case ATTACKER:
			runAttacker();
			break;
		case CHARGING:
			runCharging();
			break;
		case KILLER:
			runKiller();
			break;
		case DEFUSER:
			runDefuser();
			break;
		}
	}
	
	private void runEncampment() {
		//go to encampment
		
		//make it
	}
	
	private void runAttacker() throws GameActionException {
		if(!currentLocation.isAdjacentTo(target)) {
			mover.execute();
		} else {
			
		}
		
	}
	
	private void runCharging() throws GameActionException {
		if(!currentLocation.isAdjacentTo(target)) {
			mover.execute();
		} else {
			
		}
		
	}
	
	private void runKiller() {
//		mover.setNavType(navtype); //set to don't care about mines move!
		mover.setTarget(ENEMY_HQ);
		mover.execute();
	}
	
	private void runDefuser() {
//		mover.setNavType(navtype); //set to defuse move!
		mover.execute();
	}
}
