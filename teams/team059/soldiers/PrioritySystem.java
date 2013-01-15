package team059.soldiers;

import battlecode.common.MapLocation;
import team059.soldiers.micro.Micro;

/**
 * Makes decisions weighing priorities, map distances, and other information.
 * @author vlad
 */

public class PrioritySystem {

	private static int battleDistance = 6;
	
	public static int rate(int distance) {
		if(distance < battleDistance){
			return 1;
		}
		else{
			return 0;
		}
	}
}
