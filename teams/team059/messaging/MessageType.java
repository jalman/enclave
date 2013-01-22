package team059.messaging;

public enum MessageType {
<<<<<<< HEAD
	HQ_INFO, ATTACK_LOCATION, MICRO_INFO, CHECKPOINT_NUMBER;
=======
	HQ_INFO(1), ATTACK_LOCATION(3), CHECKPOINT_NUMBER(1), TASK_TAKEN(3), TAKE_ENCAMPMENT(3);
	/**
	 * Number of integers that comprise this message.
	 */
	public final int length;
	
	private MessageType(int length) {
		this.length = length;
	}
>>>>>>> f05e7c0b61e3912067ee7e603fa4721c54a090ad
}
