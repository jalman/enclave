package bytecodetest;

import battlecode.common.Clock;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc) {
		testForEach();
	}
	
	static void testForEach() {
		int bytecodes = Clock.getBytecodeNum();
		
		Object[] array = new Object[100];

		System.out.println(Clock.getBytecodeNum() - bytecodes);
		
		bytecodes = Clock.getBytecodeNum();

		for(int i = 0; i < array.length; i++) {
			
		}
		
		System.out.println(Clock.getBytecodeNum() - bytecodes);

		bytecodes = Clock.getBytecodeNum();
		
		for(Object o : array) {}
		
		System.out.println(Clock.getBytecodeNum() - bytecodes);		
	}
}
