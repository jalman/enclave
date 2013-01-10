package team059.utils;

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
			if(readBlock(channels[i]+index*BLOCK_SIZE, block)) {
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
	
	public void writeBlock(int index, int[] block) throws GameActionException {
		int checksum = 0;
		
		int i = 0;
		while(i < block.length) {
			rc.broadcast(index+i, block[i]);
			checksum ^= block[i];
			i++;
		}
		
		while(i < MESSAGE_SIZE) {
			checksum ^= rc.readBroadcast(index+i);
			i++;
		}
		
		rc.broadcast(index + MESSAGE_SIZE, checksum);
	}
	
	public void writeMessage(int... message) throws GameActionException {		
		int off = total_messages * BLOCK_SIZE;
		
		for(int i = 0; i < COPIES; i++) {
			writeBlock(channels[i]+off, message);
		}
		total_messages++;
	}
	
	/**
	 * Should be called by HQ at the start of each round.
	 * @throws GameActionException 
	 */
	public void initHeaderMessage() throws GameActionException {
		setChannels();
		total_messages = 0;
		valid_messages = 0;
		writeMessage(0);
	}
	
	public void writeAttackMessage(MapLocation attack, int priority) throws GameActionException {
		writeMessage(MessageType.ATTACK_LOCATION.ordinal(), attack.x, attack.y, priority);
	}
}
