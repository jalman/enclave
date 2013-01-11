package team059.utils;

public class OnePassQueue<T> {
	
	private T[][] queue;
	int[] length;
	int min = 0;
	int size = 0;
	
	@SuppressWarnings("unchecked")
	public OnePassQueue(int max_key, int max_size) {
		queue = (T[][]) new Object[max_key][max_size];
		length = new int[max_key];
	}
	
	public void insert(int key, T value) {
		queue[key][length[key]++] = value;
	}
	
	public T deleteMin() {
		while(length[min] == 0) {
			min++;
		}
		
		return queue[min][length[min]--];
	}
}
