package team059.soldiers;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.Team;
import team059.RobotBehavior;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.soldiers.micro.Micro;
import team059.utils.Shields;
import static team059.utils.Utils.*;

public class AntinukeSoldierBehavior extends SoldierBehavior2 {

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
	
	MapLocation shield = null;
	
	
	Direction attackerDirection = null;
	MapLocation target = null;
	
	int chargeCounter = 0;
	Mover mover = new Mover();
	
	AttackTask attack = new AttackTask(ENEMY_HQ,1000);
	
	public AntinukeSoldierBehavior() {
		if(Clock.getRoundNum() < 3) {
			role = Role.ENCAMPMENT;
		}/* else if (Clock.getRoundNum() < 108) {
			initAttacker();
		} else if (Clock.getRoundNum() < 158) {
			initDefuser();
		}*/ else {
			role = Role.ATTACKER;
		}
	}
	
	private void initDefuser() {
		role = Role.DEFUSER;
		mover.setTarget(ENEMY_HQ);
	}
	
	private void initAttacker() {
		role = Role.ATTACKER;
	}

	
	@Override
	public void run() throws GameActionException {
		if(shield == null) {
			shield = Shields.shieldLocations.get(0);
			attackerDirection = shield.directionTo(ALLY_HQ);
			target = shield.add(attackerDirection);
		}
		RC.setIndicatorString(2, role.name());
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
	
	private void runEncampment() throws GameActionException {
		mover.setTarget(shield);
		mover.setNavType(NavType.BUG_HIGH_DIG);
		if(!currentLocation.equals(shield)) {
			mover.execute();
		} else if (RC.isActive()) {
			RC.captureEncampment(RobotType.SHIELDS);
		}
	}
	
	private void runAttacker() throws GameActionException {
		mover.setTarget(target);
		mover.setNavType(NavType.BUG_HIGH_DIG);
		
		if(!currentLocation.isAdjacentTo(target)) {
			mover.execute();
		} else {
			Direction want = currentLocation.directionTo(target);
			if(RC.canMove(want)) {
				RC.move(want);
				role = Role.CHARGING;
			}
		}
		
	}
	
	private void runCharging() throws GameActionException {
		Direction cur = shield.directionTo(currentLocation);
		MapLocation want = shield.add(cur.rotateRight());
		Direction go = currentLocation.directionTo(want);
		//Team mine = RC.senseMine(want);
		if(RC.isActive()) {
			mover.moveMine(go);
		}
		
		if(RC.senseNearbyGameObjects(Robot.class, shield, 2, ALLY_TEAM).length > 7) {
			chargeCounter++;
		}
		
		if(chargeCounter > 14) {
			role = Role.KILLER;
		}
		
	}
	
	private void runKiller() throws GameActionException {
		mover.setNavType(NavType.BEELINE);
		attack.execute();
	}
	
	private void runDefuser() {
		mover.setNavType(NavType.BUG_HIGH_DIG);
		mover.execute();
	}
}
