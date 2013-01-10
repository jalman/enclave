package team059;

import team059.utils.MessageHandler;
import team059.utils.MessageType;
import team059.utils.MessagingSystem;
import team059.utils.Utils;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public abstract class RobotBehavior {
	protected RobotController rc;
	protected MapLocation myBase, enemyBase;
	protected MessagingSystem messagingSystem;
	protected MessageHandler[] messageHandlers;
	protected Utils utils;
	
	public RobotBehavior(RobotController rc) {
		this.rc = rc;
		myBase = rc.senseHQLocation();
		enemyBase = rc.senseEnemyHQLocation();
		
		messagingSystem = new MessagingSystem(rc);
		messageHandlers = new MessageHandler[MessageType.values().length];
		messageHandlers[MessageType.ATTACK_LOCATION.ordinal()] = getAttackHandler();
		
		utils = new Utils(rc);
	}
	
	/**
	 * Called every round.
	 */
	public abstract void run();
	
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
