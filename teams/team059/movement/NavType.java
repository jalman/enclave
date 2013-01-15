package team059.movement;

public enum NavType {
	
	ASTAR2(new AStar2()),
	BUG(new BugMoveFun()), 
	STRAIGHT_DIG(new DigMove()), 
	TANGENT_BUG, 
	RANDOM, 
	BEELINE, 
	BUG_DIG_3(new BuggingDigMove()), 
	BUG_DIG_2(new DiggingBugMoveFun2()),
	BUG_DIG_1(new DiggingBugMoveFun1());
	
	public final NavAlg navAlg;

	private NavType() {
		this.navAlg = null;
	}
	
	private NavType(NavAlg alg) {
		this.navAlg = alg;
	}
}
