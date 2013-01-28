package team059;

/**
 * Guide robot behaviors.
 * @author vlad
 *
 */
public class Parameters {
	
	/**
	 * How quickly to expand at the beginning.
	 */
	public int greed;
	
	/**
	 * Roughly defines the border between us and the enemy.
	 */
	public double border;
	
	/**
	 * Half the "width" of the border.
	 */
	public final double margin = 0.3;

	/**
	 * Priority for attacking enemy HQ.
	 */
	public int attack;
	
	/**
	 * Priority for laying mines.
	 */
	public int mine;
	
	/**
	 *A parameter for how timid microsystem is in rushing in.
	 */
	public int timidity;
	
	public Parameters(int greed, double border, int attack, int mine, int timidity) {
		this.greed = greed;
		this.border = border;
		this.attack = attack;
		this.mine = mine;
		this.timidity = timidity;
	}
}
