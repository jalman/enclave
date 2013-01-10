package team059.utils;

import team059.utils.PriorityQueue.Node;

public interface PriorityQueue<V, N extends Node<V>> {
	public N insert(int key, V value);
	public N deleteMin();
	public void decreaseKey(N node, int key);
	
	public static class Node<V> {
		int key;
		V value;
		
		public Node(int k, V v) {
			key = k;
			value = v;
		}
	}
}
