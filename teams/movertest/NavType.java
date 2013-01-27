package movertest;

public enum NavType {
	
//	ASTAR2(new AStar2()), // AStar with one-pass queue
//	ASTAR3(new AStar3()), // Local AStar with one-pass queue
	BUG(new BugMoveFun()), // Normal bug, avoid defusing
//	STRAIGHT_DIG(new DigMove()), // Try to go straight, defusing along the way
//	TANGENT_BUG,  // Tangent bug
//	RANDOM, // Wiggle randomly? 
//	BEELINE, // Go straight there, disregarding mines?
//	BUG_HIGH_DIG(new BuggingDigMove()),  // Dig-move with a bit of bugging
//	BUG_LOW_DIG(new DiggingBugMoveFunLong()), // Normal bug, defuse when bugging for too long. Probably sucks.
//	BUG_DIG_1(new DiggingBugMoveFun1()), // Normal bug, defuse when only option is to go backwards
	BUG_DIG_2(new DiggingBugMoveFun2()), // Normal bug, try defuse when trying to go away from target
//	BUG_DIG_I1(new DigBugMoveFunIterative()),
	PARTIAL_ASTAR3(new PartialAStar3()),
	ASTAR2_I1;
	
	public final NavAlg navAlg;

	private NavType() {
		this.navAlg = null;
	}
	
	private NavType(NavAlg alg) {
		this.navAlg = alg;
	}
}
