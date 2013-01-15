package team059.movement;

public enum NavType {
	
	ASTAR2(new AStar2()), // AStar with one-pass queue
	BUG(new BugMoveFun()), // Normal bug, avoid defusing
	STRAIGHT_DIG(new DigMove()), // Try to go straight, defusing along the way
	TANGENT_BUG,  // Tangent bug
	RANDOM, // Wiggle randomly? 
	BEELINE, // Go straight there, disregarding mines?
	BUG_DIG_3(new BuggingDigMove()),  // Normal bug, defuse when only option is to go backwards
	BUG_DIG_2(new DiggingBugMoveFun2()), // Normal bug, defuse when bugging for too long
	BUG_DIG_1(new DiggingBugMoveFun1()); // Dig-move with a bit of bugging
	
	public final NavAlg navAlg;

	private NavType() {
		this.navAlg = null;
	}
	
	private NavType(NavAlg alg) {
		this.navAlg = alg;
	}
}
