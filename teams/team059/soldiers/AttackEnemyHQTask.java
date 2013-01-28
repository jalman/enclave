package team059.soldiers;

import static team059.utils.Utils.*;

public class AttackEnemyHQTask extends AttackTask {

	public AttackEnemyHQTask() {
		super(ENEMY_HQ, 0);
	}

	@Override
	public int appeal() {
		return parameters.attack + super.appeal();
	}

	@Override
	public boolean done() {
		return false;
	}
}
