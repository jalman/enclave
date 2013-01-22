package team059.soldiers;

import team059.Task;
import team059.utils.ArraySet;

public class TaskManager extends TaskGiver {

	private ArraySet<Task> tasks = new ArraySet<Task>(100);
	private Task currentTask = null;

	public void insertTask(Task t) {
		tasks.insert(t);
		compute();
	}

	@Override
	public void compute() {
		int max_appeal = Integer.MIN_VALUE;
		for(int i = 0; i < tasks.size; i++) {
			Task t = tasks.get(i);
			if(t.done()) {
				tasks.delete(i);
			} else {
				int appeal = t.appeal();
				if(appeal > max_appeal) {
					currentTask = t;
					max_appeal = appeal;
				}
			}
		}

	}

	@Override
	public Task getTask() {
		return currentTask;
	}

}
