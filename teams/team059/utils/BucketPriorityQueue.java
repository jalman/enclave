package team059.utils;

import team059.utils.PriorityQueue.Node;
import team059.utils.BucketPriorityQueue.BucketNode;

/**
 * A one-pass priority queue using buckets.
 * @author vlad
 *
 * @param <V> The value type.
 */
public class BucketPriorityQueue<V> implements PriorityQueue<V, BucketNode<V>> {

	private final BucketNode<V>[] queue;
	public int size;
	private int min;
	
	@SuppressWarnings("unchecked")
	public BucketPriorityQueue(int max_weight) {
		queue = new BucketNode[max_weight];
		size = 0;
		min = 0;
	}
	
	public static class BucketNode<V> extends Node<V> {
		private BucketNode<V> prev, next;
		public BucketNode(int k, V v) {
			super(k, v);
		}
	}

	@Override
	public BucketNode<V> insert(int key, V value) {
		BucketNode<V> node = new BucketNode<V>(key, value);
		insert(node);
		return node;
	}

	private void insert(BucketNode<V> node) {
		BucketNode<V> head = queue[node.key];
		if(head == null) {
			queue[node.key] = node.prev = node.next = node;
		} else {
			node.next = head.next;
			head.next = node;
			node.next.prev = node;
			node.prev = head;
		}
		size++;
	}
	
	@Override
	public BucketNode<V> deleteMin() {
		while(queue[min] == null) {
			min++;
		}
		
		BucketNode<V> node = queue[min];
		delete(node);
		return node;
	}
	
	private void delete(BucketNode<V> node) {
		if(node.next == node) {
			queue[node.key] = null;
		} else {
			node.next.prev = node.prev;
			node.prev.next = node.next;
		}
		size--;
	}

	/**
	 * It is crucial that this method does not decrease the key to below
	 * that from the last call to deleteMin().
	 */
	@Override
	public void decreaseKey(BucketNode<V> node, int key) {
		delete(node);
		node.key = key;
		insert(node);
	}
}
