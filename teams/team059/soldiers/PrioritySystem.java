package team059.soldiers;

import battlecode.common.MapLocation;
import team059.soldiers.micro.Micro;

/**
 * Makes decisions weighing priorities, map distances, and other information.
 * @author vlad
 */
public class PrioritySystem {
	public static int rate(int distance) {
		if(distance < Micro.battleDistance){
			return 1;
		}
		else{
			return 0;
		}
	}
}
