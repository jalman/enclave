package team059;

import team059.messaging.MessageHandler;
import team059.messaging.MessageType;
import team059.messaging.MessagingSystem;
import team059.utils.ArraySet;
import static team059.utils.Utils.*;
import battlecode.common.GameActionException;

public class RobotBehavior {

	public static final int TASK_SWITCH_THRESHOLD = 5;
	
	/**
	 * Whether we want to send messages this round.
	 */
	public boolean messaging;
	public MessagingSystem messagingSystem;
	protected MessageHandler[] messageHandlers;

	protected ArraySet<Task> pendingTasks;
	protected Task currentTask = null;
	
	public RobotBehavior() {
		messagingSystem = new MessagingSystem();
		messageHandlers = new MessageHandler[MessageType.values().length];
		messageHandlers[MessageType.HQ_INFO.ordinal()] = getHQHandler();		
		messageHandlers[MessageType.ATTACK_LOCATION.ordinal()] = getAttackHandler();
		messageHandlers[MessageType.CHECKPOINT_NUMBER.ordinal()] = getCheckpointHandler();
		
		pendingTasks = new ArraySet<Task>(1000);
	}

	/**
	 * Initializes the persistent tasks.
	 */
	public void initTasks() {};
	
	/**
	 * Called at the beginning of each round.
	 * By default, reads and handles messages.
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
			messagingSystem.handleMessages(messageHandlers);
		}
	}

	/**
	 * Chooses new tasks each round.
	 */
	protected void chooseTasks() {}
	
	/**
	 * Called every round.
	 * @throws GameActionException 
	 */
	public void run() throws GameActionException {
		if(!RC.isActive()) return;
		chooseTasks();
		
		int max_appeal = currentTask != null ? currentTask.appeal()  + TASK_SWITCH_THRESHOLD : 0;
		
		for(int i = 0; i < pendingTasks.size; i++) {
			Task task = pendingTasks.get(i);
			int appeal = task.appeal();
			
			if(appeal < 0) {
				pendingTasks.delete(i);
			} else if(appeal > max_appeal) {
				max_appeal = appeal;
				if(currentTask == null) {
					currentTask = task;
					pendingTasks.delete(i);
				} else {
					Task swap = currentTask;
					currentTask = task;
					pendingTasks.set(i, swap);
				}
			}
		}
		
		if(currentTask != null && currentTask.execute()) {
			currentTask = null;
		}
	}

	/**
	 * Called at the end of each round.
	 * By default, writes the header message.
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
	protected MessageHandler getHQHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				strategy = Strategy.values()[message[1]];
				//System.out.println(strategy);
			}
		};
	}

	/**
	 * Override in order to respond to this type of message.
	 * @return The default message handler (does nothing).
	 */
	protected MessageHandler getCheckpointHandler() {return new DefaultMessageHandler();}	
}
