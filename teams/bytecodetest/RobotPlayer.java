package bytecodetest;

import battlecode.common.Clock;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc) {
		testForEach();
	}
	
	static void testForEach() {

		int min_x = 50, min_y = 50, max_x = 100, max_y = 100;
		int tot = 0;

		int bytecodes = Clock.getBytecodeNum();
		for(int i=49; i<101; i++) {
			for(int j=49; j<1; j++) {
				if(i < min_x || i >= max_x || j < min_y || j >= max_y) {
					tot++;
				}
			}
		}
		int bcused = Clock.getBytecodeNum() - bytecodes;
		System.out.println("tot = " + tot + ", bcused = " + bcused);
		
	}
}
