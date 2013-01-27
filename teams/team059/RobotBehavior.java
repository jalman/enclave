package team059;

import team059.messaging.MessageHandler;
import team059.messaging.MessageType;
import team059.messaging.MessagingSystem;
import static team059.utils.Utils.*;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public class RobotBehavior {

	protected MessageHandler[] messageHandlers;
	protected Strategy strategy;

	public RobotBehavior() {
		messageHandlers = new MessageHandler[MessageType.values().length];
		messageHandlers[MessageType.HQ_INFO.ordinal()] = getHQHandler();		
		messageHandlers[MessageType.ATTACK_LOCATION.ordinal()] = getAttackHandler();
		messageHandlers[MessageType.CHECKPOINT_NUMBER.ordinal()] = getCheckpointHandler();
		messageHandlers[MessageType.MICRO_INFO.ordinal()] = getMicroHandler();
		messageHandlers[MessageType.TAKE_ENCAMPMENT.ordinal()] = getTakeEncampmentHandler();
		messageHandlers[MessageType.BIRTH_INFO.ordinal()] = getBirthInfoHandler();
		messageHandlers[MessageType.LAYING_MINE.ordinal()] = getLayingMineHandler();
		messageHandlers[MessageType.DEFUSING_MINE.ordinal()] = getDefusingMineHandler();
		messageHandlers[MessageType.ANNOUNCE_UPGRADE.ordinal()] = getAnnounceUpgradeHandler();
		messageHandlers[MessageType.TAKING_ENCAMPMENT.ordinal()] = getTakingEncampmentHandler();
	}

	protected int danger(MapLocation loc) {return 0;}

	/**
	 * Called at the beginning of each round.
	 */
	public void beginRound() throws GameActionException {
		try {
			messagingSystem.beginRound(messageHandlers);
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called every round.
	 */
	public void run() throws GameActionException {}

	/**
	 * Called at the end of each round.
	 */
	public void endRound() {
		try {
			messagingSystem.endRound();
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

	/**
	 * Reads the strategy from the HQ.
	 * @return The default HQ-message handler.
	 */
	protected MessageHandler getHQHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				strategy = Strategy.values()[message[0]];
				//System.out.println(strategy);
			}
		};
	}

	/**
	 * Override in order to respond to this type of message.
	 * @return The default message handler (does nothing).
	 */
	protected MessageHandler getCheckpointHandler() {return new DefaultMessageHandler();}	

	protected MessageHandler getMicroHandler() {return new DefaultMessageHandler();}

	protected MessageHandler getTakeEncampmentHandler() {return new DefaultMessageHandler();}	

	protected MessageHandler getBirthInfoHandler() {return new DefaultMessageHandler();}

	protected MessageHandler getLayingMineHandler() {return new DefaultMessageHandler();}

	protected MessageHandler getDefusingMineHandler() {return new DefaultMessageHandler();}	

	protected MessageHandler getAnnounceUpgradeHandler() {return new DefaultMessageHandler();}

	protected MessageHandler getTakingEncampmentHandler() {return new DefaultMessageHandler();}
}
