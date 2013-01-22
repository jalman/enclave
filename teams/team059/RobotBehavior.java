package team059;

import team059.messaging.MessageHandler;
import team059.messaging.MessageType;
import team059.messaging.MessagingSystem;
import team059.soldiers.TaskGiver;
import team059.utils.ArraySet;
import static team059.utils.Utils.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;

public class RobotBehavior {

	public static final int TASK_SWITCH_THRESHOLD = 10;
	
	/**
	 * Whether we want to send messages this round.
	 */
	public boolean messaging;
	public MessagingSystem messagingSystem;
	protected MessageHandler[] messageHandlers;

	protected ArraySet<Task> pendingTasks;
	protected TaskGiver[] taskGivers = new TaskGiver[0];
	protected Task currentTask = null;
	
	public RobotBehavior() {
		messagingSystem = new MessagingSystem();
		messageHandlers = new MessageHandler[MessageType.values().length];
		messageHandlers[MessageType.HQ_INFO.ordinal()] = getHQHandler();		
		messageHandlers[MessageType.ATTACK_LOCATION.ordinal()] = getAttackHandler();
		messageHandlers[MessageType.CHECKPOINT_NUMBER.ordinal()] = getCheckpointHandler();
		messageHandlers[MessageType.TAKE_ENCAMPMENT.ordinal()] = getTakeEncampmentHandler();
		
		pendingTasks = new ArraySet<Task>(1000);
	}

	/**
	 * Called at the beginning of each round.
	 * By default, reads and handles messages.
	 */
	public void beginRound() throws GameActionException {
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
	 * Add tasks to the list of pending tasks.
	 */
	public void chooseTasks() {}
	
	/**
	 * Called every round.
	 * @throws GameActionException 
	 */
	public void run() throws GameActionException {
		if(!RC.isActive()) return;
		
		if(currentTask.done()) {
			currentTask = null;
		}
		
		int max_appeal;
		
		if(currentTask == null || Clock.getRoundNum() % 25 == 0) {
			chooseTasks();
		}
		
		max_appeal = currentTask != null ? currentTask.appeal() + TASK_SWITCH_THRESHOLD : 0;
		
		for(int i = 0; i < pendingTasks.size; i++) {
			Task t = pendingTasks.get(i);
			if(t.done()) {
				pendingTasks.delete(i);
				continue;
			}
			
			int a = t.appeal();
			if(a > max_appeal) {
				currentTask = t;
				max_appeal = a;
			}
		}
		
		currentTask.execute();
	}

	/**
	 * Called at the end of each round.
	 * By default, writes the header message.
	 */
	public void endRound() throws GameActionException {
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
	 * Receives the current strategy from the HQ.
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
	
	
	/**
	 * Override in order to respond to this type of message.
	 * @return The default message handler (does nothing).
	 */
	protected MessageHandler getTakeEncampmentHandler() {return new DefaultMessageHandler();}	
	
}
