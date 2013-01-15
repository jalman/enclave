package team059;

import team059.messaging.MessageHandler;
import team059.messaging.MessageType;
import team059.messaging.MessagingSystem;
import static team059.utils.Utils.*;
import battlecode.common.Clock;
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
	public Strategy strategy;
	
	/**
	 * Whether we want to send messages this round.
	 */
	public boolean messaging;
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

		messagingSystem = new MessagingSystem();
		messageHandlers = new MessageHandler[MessageType.values().length];
		messageHandlers[MessageType.HQ_INFO.ordinal()] = getHQHandler();		
		messageHandlers[MessageType.ATTACK_LOCATION.ordinal()] = getAttackHandler();
		messageHandlers[MessageType.BATTLE_INFO.ordinal()] = getBattleHandler();
	}

	protected int danger(MapLocation loc) {return 0;}

	/**
	 * Called at the beginning of each round.
	 */
	public void beginRound() {
		messaging = RC.getTeamPower() > MessagingSystem.MESSAGING_COST;
		//messaging = false;
		
		if(messaging) {
			try {
				messagingSystem.beginRound();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
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
		if(messaging) {
			try {
				messagingSystem.endRound();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
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
	
	/**
	 * Override in order to respond to this type of message.
	 * @return The default message handler (does nothing).
	 */
	protected MessageHandler getHQHandler() {return new DefaultMessageHandler();}	

	/**
	 * Override in order to respond to this type of message.
	 * @return The default message handler (does nothing).
	 */
	protected MessageHandler getBattleHandler() {return new DefaultMessageHandler();}	
}
