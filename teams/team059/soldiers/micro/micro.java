

Team enemy;
boolean retreatMode;

boolean enemyNearby() // tests whether an enemy is closeby.
{
	if (senseNearbyGameObjecs(RobotType.SOLDIER, 14, enemy).length == 0) // condition
	{
		return false;
	}
	return true;
}

boolean hasEnoughAllies() // tests whether there are enough allies nearby to engage.
{
	if(true)// wants to have allies who can sense enemies. Use the messaging system to do this.
	{
		return true;
	}
	return false;
}

// two modes; normal mode
void battleState() // determines whether to retreat or not. Run battlestate every turn.
{
	if(enemyNearby() && !hasEnoughAllies())
	{
		retreatMode = true;
	}
	else
	{
		retreatMode = false;
	}
}

void retreat()
{
	if (retreatMode == true)
	{
		
	}
}
