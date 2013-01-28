package team059.soldiers;

public class SingleTaskManager<T extends Task> extends TaskGiver {

	private T currentTask;
	
	public SingleTaskManager() {
		currentTask = null;
	}
	
	public SingleTaskManager(T task) {
		currentTask = task;
	}
	
	@Override
	public void compute() {
		if(currentTask != null && currentTask.done()) {
			currentTask = null;
		}
	}

	@Override
	public T getTask() {
		return currentTask;
	}
	
	public void clearTask() {
		currentTask = null;
	}
	
	public void considerTask(T task) {
		if(currentTask == null || task.appeal() > currentTask.appeal()) {
			currentTask = task;
		}
	}

}
