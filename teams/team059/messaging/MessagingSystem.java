package team059.messaging;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * Securely send and receive messages.
 * @author vlad
 */
public class MessagingSystem {
	
	public static final int MESSAGE_SIZE = 5;
	public static final int BLOCK_SIZE = MESSAGE_SIZE+1;
	
	private static final int COPIES = 2;
	private static final int DISPLACEMENT = GameConstants.BROADCAST_MAX_CHANNELS / COPIES;
	
	private final RobotController rc;
	
	/**
	 * Stores messages that are read each round.
	 */
	public final int[][] buffer = new int[100][MESSAGE_SIZE];
	/**
	 * The total number of messages posted by our team.
	 */
	public int total_messages;
	/**
	 * The number of valid messages read this round.
	 */
	public int valid_messages;
	
	public final int[] channels = new int[COPIES];
	
	public MessagingSystem(RobotController rc) {
		this.rc = rc;
	}
	
	public int key(int index) {return 0;}
	
	/**
	 * Sets up the channels for communication. Should be called each round.
	 */
	public void setChannels() {
		int k = key(Clock.getRoundNum());
		for(int i = 0; i < COPIES; i++) {
			channels[i] = (k + i * DISPLACEMENT) % GameConstants.BROADCAST_MAX_CHANNELS;
		}
	}
	
	/**
	 * Reads a block from the global message board.
	 * @param channel The location of the block.
	 * @param block Stores the block.
	 * @return Whether the block is uncorrupted.
	 * @throws GameActionException
	 */
	public boolean readBlock(int channel, int[] block) throws GameActionException {
		int checksum = 0;
		for(int i = 0; i < MESSAGE_SIZE; i++) {
			block[i] = rc.readBroadcast(channel+i);
			checksum ^= block[i];
		}
		
		return checksum == rc.readBroadcast(channel + MESSAGE_SIZE);
	}
	
	/**
	 * Tries to read a single message, using multiple channels of communication.
	 * @param index Index of the message among the set of messages.
	 * @param block Stores the message.
	 * @return Whether the message was successfully read.
	 * @throws GameActionException
	 */
	private boolean readMessage(int index, int[] block) throws GameActionException {
		for(int i = 0; i < COPIES; i++) {
			int off = index * BLOCK_SIZE;
			if(readBlock(channels[i]+off, block)) {
				//fix messages
				for(int j = 0; j < COPIES; j++) {
					if(j == i) continue;
					writeBlock(channels[j]+off, block);
				}
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Reads the messages posted by our team this round.
	 * These are stored in the {@link #buffer}.
	 * This method should be called once per turn.
	 * @throws GameActionException
	 */
	public void readMessages() throws GameActionException {
		setChannels();
		
		valid_messages = 0;
		
		int[] header = new int[MESSAGE_SIZE];
		if(!readMessage(0, header)) return;
		
		total_messages = header[0]+1;
		
		for(int i = 1; i < total_messages; i++) {
			if(readMessage(i, buffer[valid_messages]))
				valid_messages++;
		}
	}
	
	/**
	 * Convenience method for handling messages.
	 * @param handlers Specify how to handle each type of message.
	 */
	public void handleMessages(MessageHandler[] handlers) {
		for(int i = 0; i < valid_messages; i++) {
			int[] message = buffer[i];
			handlers[message[0]].handleMessage(message);
		}
	}
	
	public void writeBlock(int channel, int[] block) throws GameActionException {
		int checksum = 0;
		
		int i = 0;
		while(i < block.length) {
			rc.broadcast(channel+i, block[i]);
			checksum ^= block[i];
			i++;
		}
		
		while(i < MESSAGE_SIZE) {
			checksum ^= rc.readBroadcast(channel+i);
			i++;
		}
		
		rc.broadcast(channel + MESSAGE_SIZE, checksum);
	}
	
	/**
	 * Writes a message to the global radio.
	 * @param message The message data. The first parameter must be the MessageType.ordinal().
	 * @throws GameActionException
	 */
	public void writeMessage(int... message) throws GameActionException {		
		int off = total_messages * BLOCK_SIZE;
		
		for(int i = 0; i < COPIES; i++) {
			writeBlock(channels[i]+off, message);
		}
		total_messages++;
	}
	
	private int prev_messages = 0;
	
	/**
	 * Reads messages from last round, and initializes the new header message.
	 * Should be called by HQ at the start of each round.
	 * @throws GameActionException 
	 */
	public void initHeaderMessage() throws GameActionException {
		//do something with messages from last round?
		if(Clock.getRoundNum() > 0) {
			readMessages();
			prev_messages = valid_messages;
		}		
		
		setChannels();
		total_messages = 0;
		writeMessage(0);
	}
	
	public void writeAttackMessage(MapLocation attack, int priority) throws GameActionException {
		writeMessage(MessageType.ATTACK_LOCATION.ordinal(), attack.x, attack.y, priority);
	}
}
