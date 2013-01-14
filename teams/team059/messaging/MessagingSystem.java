package team059.messaging;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import static team059.utils.Utils.*;

/**
 * Securely send and receive messages.
 * @author vlad
 */
public class MessagingSystem {
	
	public static final int MESSAGE_SIZE = 5;
	public static final int BLOCK_SIZE = MESSAGE_SIZE+1;
	
	private static final int MAX_CHANNEL = GameConstants.BROADCAST_MAX_CHANNELS - 1024;
	private static final int COPIES = 2;
	private static final int DISPLACEMENT = MAX_CHANNEL / COPIES;
	
	public static final double MESSAGING_COST = 2;
	
	/**
	 * The channels of communication. These are set each round.
	 */
	public final int[] channels = new int[COPIES];
	
	/**
	 * Stores messages that are read each round.
	 */
	public final int[][] buffer = new int[500][MESSAGE_SIZE];
	
	/**
	 * Stores the header message. This is not placed in the buffer.
	 */
	private final int[] header = new int[MESSAGE_SIZE];

	/**
	 * The total number of messages posted by our team. Includes the header message.
	 */
	public int total_messages;
	/**
	 * The number of valid messages read this round.
	 */
	public int valid_messages;
	/**
	 * Whether any messages were written this round.
	 */
	private boolean message_written;
	
	public MessagingSystem() {}
	
	public int key(int seed) {
		Random r = new Random(seed);
		return r.nextInt();
	}
	
	/**
	 * Sets up the channels for communication. Should be called each round.
	 */
	private void setChannels() {
		int k = key(Clock.getRoundNum());
		for(int i = 0; i < COPIES; i++) {
			channels[i] = (k + i * DISPLACEMENT) % MAX_CHANNEL;
			if(channels[i] < 0) channels[i] += MAX_CHANNEL;
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
			block[i] = RC.readBroadcast(channel+i);
			checksum += block[i];
		}
		
		return checksum == RC.readBroadcast(channel + MESSAGE_SIZE);
	}
	
	public boolean checkBlock(int channel) throws GameActionException {
		int checksum = 0;
		for(int i = 0; i < MESSAGE_SIZE; i++) {
			checksum += RC.readBroadcast(channel+i); 
		}
		
		return checksum == RC.readBroadcast(channel + MESSAGE_SIZE);
	}
	
	/**
	 * Tries to read a single message, using multiple channels of communication.
	 * If an uncorrupted copy is found, the other copies are checked and fixed.
	 * @param index Index of the message among the set of messages.
	 * @param block Stores the message.
	 * @return Whether the message was successfully read.
	 * @throws GameActionException
	 */
	private boolean readMessage(int index, int[] block) throws GameActionException {
		int off = index * BLOCK_SIZE;
		for(int i = 0; i < COPIES; i++) {
			if(readBlock(channels[i]+off, block)) {
				//fix messages
				for(int j = 0; j < i; j++) {
					writeBlock(channels[j]+off, block);
				}
				
				for(int j = i+1; j < COPIES; j++) {
					if(!checkBlock(channels[j]+off))
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
		readMessagesNoChannels();
	}
	
	//doesn't set the channels
	private void readMessagesNoChannels() throws GameActionException {		
		valid_messages = 0;
		
		if(!readMessage(0, header)) return;
		
		total_messages = header[0];
		
		
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
			RC.broadcast(channel+i, block[i]);
			checksum += block[i];
			i++;
		}
		
		while(i < MESSAGE_SIZE) {
			checksum += RC.readBroadcast(channel+i);
			i++;
		}
		
		RC.broadcast(channel + MESSAGE_SIZE, checksum);
	}
		
	/**
	 * Writes a message to the global radio.
	 * @param index The index at which to write the message.
	 * @param message The message data.
	 * @throws GameActionException
	 */
	public void writeMessageAtIndex(int index, int... message) throws GameActionException {		
		int off = index * BLOCK_SIZE;
		
		for(int i = 0; i < COPIES; i++) {
			writeBlock(channels[i]+off, message);
		}
		total_messages++;
		message_written = true;
	}
	
	/**
	 * Writes a message to the global radio.
	 * @param type The type of message.
	 * @param message The message data.
	 * @throws GameActionException
	 */
	public void writeMessage(int... message) throws GameActionException {		
		int off = total_messages * BLOCK_SIZE;
		
		for(int i = 0; i < COPIES; i++) {
			writeBlock(channels[i]+off, message);
		}
		total_messages++;
		message_written = true;
	}
	
	
	/**
	 * Rewrites the header message. Should be called at the end of each round by any robot that uses messaging.
	 * @throws GameActionException 
	 */
	public void writeHeaderMessage() throws GameActionException {
		if(message_written) {
			writeMessageAtIndex(0, total_messages);
		}
		message_written = false;
	}
	
	/**
	 * Reads messages from last round, and initializes the new header message.
	 * Should be called by HQ at the start of each round, instead of {@link #readMessages()}.
	 * @throws GameActionException 
	 */
	public void initMessagingSystem() throws GameActionException {
		valid_messages = 0;
		
		//do something with messages from last round?
		if(Clock.getRoundNum() > 0) {
			readMessagesNoChannels();
		}
		
		setChannels();
		total_messages = 1;
		message_written = true;
	}
	
	public void writeAttackMessage(MapLocation loc, int priority) throws GameActionException {
		writeMessage(MessageType.ATTACK_LOCATION.ordinal(), loc.x, loc.y, priority);
	}
	
	public void debug() throws GameActionException {
		for(int i = 0; i < total_messages; i++) {
			int off = i * BLOCK_SIZE;
			for(int j = 0; j < BLOCK_SIZE; j++) {
				System.out.print(RC.readBroadcast(channels[0] + off + j) + " ");
			}
			System.out.println();
		}
	}
	
	public void scramble(int start, int end) {
		start = start < 0 ? 0 : start;
		end = end > 9999 ? 9999 : end;
		for(int i = start; i < end; i++) {
			if(!isAChannel(i)) {
				try {
					RC.broadcast(i,0);
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean isAChannel(int i) {
		for(int j = 0; j < channels.length; j++) {
			if(channels[i] == i) {
				return true;
			}
		}
		return false;
	}
}
