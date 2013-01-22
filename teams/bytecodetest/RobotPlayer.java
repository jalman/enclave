package bytecodetest;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc) {
		//Utils.initUtils(rc);
		testTryCatch();
		//testInstantiation();
		//testArrayAccess();
		//testIf();
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
