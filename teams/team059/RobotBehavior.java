package team059;

import team059.messaging.MessageHandler;
import team059.messaging.MessageType;
import team059.messaging.MessagingSystem;
import static team059.utils.Utils.*;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class RobotBehavior {
	public final RobotController rc;
	public final Team myTeam;
	public final Team enemyTeam;
	public final MapLocation myBase, enemyBase;
	public final int width, height;
	
	protected MessagingSystem messagingSystem;
	protected MessageHandler[] messageHandlers;
	
	public RobotBehavior(RobotController rc) {
		this.rc = rc;
		myTeam = rc.getTeam();
		enemyTeam = (myTeam == Team.A) ? Team.B : Team.A;
		myBase = rc.senseHQLocation();
		enemyBase = rc.senseEnemyHQLocation();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		
		messagingSystem = new MessagingSystem(rc);
		messageHandlers = new MessageHandler[MessageType.values().length];
		messageHandlers[MessageType.ATTACK_LOCATION.ordinal()] = getAttackHandler();
	}
	
	protected int danger(MapLocation loc) {return 0;}
	
	/**
	 * Called at the beginning of each round.
	 */
	public void beginRound() {
		try {
			messagingSystem.readMessages();
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Called every round.
	 */
	public void run() {}
	
	/**
	 * Called at the end of each round.
	 */
	public void endRound() {
		try {
			messagingSystem.writeHeaderMessage();
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	private static class DefaultMessageHandler implements MessageHandler {
		@Override
		public void handleMessage(int[] message) {}
	}
	
	/**
	 * Override in order to respond to this type of message.
	 * @return The default message handler (does nothing).
	 */
	protected MessageHandler getAttackHandler() {return new DefaultMessageHandler();}
}
