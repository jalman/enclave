package team059.soldiers;

public class SingleTaskManager extends TaskGiver {

	private Task currentTask;
	
	@Override
	public void compute() {
		if(currentTask != null && currentTask.done()) {
			currentTask = null;
		}
	}

	@Override
	public Task getTask() {
		return currentTask;
	}
	
	
	public void considerTask(Task task) {
		if(currentTask == null || task.appeal() > currentTask.appeal()) {
			currentTask = task;
		}
	}

}
