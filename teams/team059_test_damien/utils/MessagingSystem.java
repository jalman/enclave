package team059.utils;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

/**
 * Securely send and receive messages.
 * @author vlad
 */
public class MessagingSystem {
	
	public static final int BLOCK_SIZE = 5;
	
	private static final int COPIES = 2;
	private static final int DISPLACEMENT = 10000/COPIES;
	
	private final RobotController rc;
	
	public final int[][] buffer = new int[100][BLOCK_SIZE];
	public int total_messages;
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
		channels[0] = key(Clock.getRoundNum());
		for(int i = 0; i < COPIES; i++) {
			channels[i] += i * DISPLACEMENT;
		}
	}
	
	public boolean readBlock(int index, int[] block) throws GameActionException {
		int checksum = 0;
		for(int i = 0; i < BLOCK_SIZE; i++) {
			block[i] = rc.readBroadcast(index+i);
			checksum ^= block[i];
		}
		
		return checksum == rc.readBroadcast(index + BLOCK_SIZE);
	}
	
	/**
	 * Tries to read a single message, using multiple channels of communication.
	 * @param off Offset from the channel origin.
	 * @param block Stores the message.
	 * @return Whether the message was successfully read.
	 * @throws GameActionException
	 */
	private boolean readMessage(int off, int[] block) throws GameActionException {
		for(int i = 0; i < COPIES; i++) {
			if(readBlock(channels[i]+off, block)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Should be called once per turn.
	 * @throws GameActionException
	 */
	public void readMessages() throws GameActionException {
		int off = 0;
		valid_messages = 0;
		
		int[] header = new int[BLOCK_SIZE];
		if(!readMessage(off, header)) return;
		
		total_messages = header[0];
		
		for(int i = 0; i < total_messages; i++) {
			off += BLOCK_SIZE + 1;
			if(readMessage(off, buffer[valid_messages]))
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
		
		while(i < BLOCK_SIZE) {
			checksum ^= rc.readBroadcast(index+i);
			i++;
		}
		
		rc.broadcast(index + BLOCK_SIZE, checksum);
	}
	
	public void writeMessage(int... message) throws GameActionException {
		total_messages++;
		int off = total_messages * (BLOCK_SIZE+1);
		
		for(int i = 0; i < COPIES; i++) {
			writeBlock(channels[i]+off, message);
		}
	}
}
