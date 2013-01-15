package team059_presprint.messaging;

public class Message {
	public static final int MAX_SIZE = 10;
	
	public MessageType type;
	public int[] message = new int[MAX_SIZE];
}
