package team059.soldiers;

/**
 * Makes decisions weighing priorities, map distances, and other information.
 * @author vlad
 */
public class PrioritySystem {
	public static int rate(int priority, int distance) {
		return priority * 10 - distance;
	}
}
