package team059.messaging;

public enum MessageType {

	HQ_INFO(1), ATTACK_LOCATION(3), CHECKPOINT_NUMBER(1), TASK_TAKEN(3), TAKE_ENCAMPMENT(3), MICRO_INFO(3), BIRTH_INFO(4);
	/**
	 * Number of integers that comprise this message.
	 */
	public final int length;
	
	private MessageType(int length) {
		this.length = length;
	}
}
