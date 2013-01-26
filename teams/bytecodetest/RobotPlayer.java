package bytecodetest;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc) {
		//Utils.initUtils(rc);
		//testTryCatch();
		//testInstantiation();
		//testArrayAccess();
		//testIf();
		testArrayInit();
	}
	
	public static void testArrayInit() { // Initializing non-empty array takes 5 + 4*array.length bytecodes
		int bc = Clock.getBytecodeNum();
		final int[] SQUARES_IN_RANGE_98 = 
			{1, 5, 9, 9, 13, 21, 21, 21, 
			25, 29, 37, 37, 37, 45, 45, 
			45, 49, 57, 61, 61, 69, 69, 
			69, 69, 69, 81, 89, 89, 89, 
			97, 97, 97, 101, 101, 109, 
			109, 113, 121, 121, 121, 129, 
			137, 137, 137, 137, 145, 145, 
			145, 145, 149, 161, 161, 169, 
			177, 177, 177, 177, 177, 185, 
			185, 185, 193, 193, 193, 197, 
			213, 213, 213, 221, 221, 221, 
			221, 225, 233, 241, 241, 241, 
			241, 241, 241, 249, 253, 261, 
			261, 261, 277, 277, 277, 277, 
			285, 293, 293, 293, 293, 293, 
			293, 293, 301, 305};
		int bc2  = Clock.getBytecodeNum();
		System.out.println("used " + (bc2 - bc));
		bc = Clock.getBytecodeNum();
		final int[] SQUARES_IN_RANGE_49 = 
			{1, 5, 9, 9, 13, 21, 21, 21, 25, 29, 37, 37, 37, 45, 
				45, 45, 49, 57, 61, 61, 69, 69, 69, 69, 69, 81, 
				89, 89, 89, 97, 97, 97, 101, 101, 109, 109, 113, 
				121, 121, 121, 129, 137, 137, 137, 137, 145, 145, 145, 145, 149};
		bc2 = Clock.getBytecodeNum();
		System.out.println("used " + (bc2 - bc));
		
		System.out.println(SQUARES_IN_RANGE_49.length);
		

		bc = Clock.getBytecodeNum();
		final int[] SQUARES_IN_RANGE_blah = new int[50];
		bc2 = Clock.getBytecodeNum();
		System.out.println("used " + (bc2 - bc));

	}
	
	public static void testTryCatch() {
		Object[] array = new Object[1];
		Object object;
		
		System.out.println(Clock.getBytecodeNum());
		
		try {
			object = array[1];
		} catch(Exception e) {
			
		}
		System.out.println(Clock.getBytecodeNum());
		
	}
	
	public static void testIf() {
		int x = 1, y = 2;
		
		boolean valid = false;

		System.out.println(Clock.getBytecodeNum());
		
		if(valid) {
			x = 2;
		}
		
		System.out.println(Clock.getBytecodeNum());

	}
	
	public static void testArrayAccess() {
		int[][] a2 = new int[10][10];
		int[] a1;
		int a0;

		System.out.println(Clock.getBytecodeNum());
		a1 = a2[0];
		System.out.println(Clock.getBytecodeNum());
		a0 = a2[0][0];
		System.out.println(Clock.getBytecodeNum());
	}
	
	public static void testInstantiation() {
		Object a;
		System.out.println(Clock.getBytecodeNum());
		a = new Object();
		System.out.println(Clock.getBytecodeNum());
		a = new Object[10][10];
		System.out.println(Clock.getBytecodeNum());
	}
	
	public static void testMapLocation() {
		int x = 0, y = 0;
		MapLocation loc;
		
		System.out.println(Clock.getBytecodeNum());

		loc = new MapLocation(x, y);
		
		System.out.println(Clock.getBytecodeNum());
		
		loc = Utils.mapLocation(x, y);
		
		System.out.println(Clock.getBytecodeNum());

		loc = Utils.mapLocation(x, y);
		
		System.out.println(Clock.getBytecodeNum());
		
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
