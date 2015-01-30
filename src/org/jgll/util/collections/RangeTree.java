package org.jgll.util.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.util.collections.RangeTree.Node;

public class RangeTree<T> implements Iterable<Node<T>>{	

	private Node<T> root;
	
	private int countNodes;
	
	public RangeTree(CharacterRange range, T val) {
		this.root = new Node<>(range.getStart(), range.getEnd(), val);;
	}
	
	public Node<T> getRoot() {
		return root;
	}
	
	public T get(int key) {
		return get(key, root);
	}
	
	public T get(int key, Node<T> node) {
		if (node == null)
			return null;
		
		if (key < node.start) 
			return get(key, node.left);
		
		if (key > node.end)
			return get(key, node.right);
		
		return node.val;
	}
	
	public void insert(CharacterRange key, T val) {
		insert(key, val, root, new ArrayList<>());
	}
	
	private void insert(CharacterRange range, T val, Node<T> node, List<Node<T>> insertionPath) {		
		insertionPath.add(node);
		if (range.getStart() < node.start) {
			if (node.left == null) {
				node.left = new Node<>(range.getStart(), range.getEnd(), val);
				countNodes++;
				if (insertionPath.size() > 1)
					balance(insertionPath);
			} else {
				insert(range, val, node.left, insertionPath);
			}
		} 
		else if (range.getEnd() > node.end) {
			if (node.right == null) {
				node.right = new Node<>(range.getStart(), range.getEnd(), val);
				countNodes++;
				if (insertionPath.size() > 1) 
					balance(insertionPath);
			} else {
				insert(range, val, node.right, insertionPath);					
			}
		} 
	}
	 
	private void balance(List<Node<T>> insertionPath) {
		for (int i = insertionPath.size() - 1; i >= 0; i--) {
			Node<T> node = insertionPath.get(i);
			node.updateHeight();
			if (!node.isBalanced()) {
				if (node.isRightHeavy()) {
					leftRotate(node);
				}
			}
		}
	}

	/**
	 *      x                y
	 *     / \              / \
	 *    A   y     =>     x   C 
	 *       / \          / \
	 *      B   C        A   B
	 */
	private Node<T> leftRotate(Node<T> x) {
		Node<T> y = x.right;
		Node<T> B = y.left;
		y.left = x;
		x.right = B;
		return y;
	}
	
	/**
	 *      x                y
	 *     / \              / \
	 *    y   C     =>     A   x 
	 *   / \                  / \
	 *  A   B                B   C
	 *      
	 */
	private Node<T> rightRotate(Node<T> x) {
		Node<T> y = x.left;
		Node<T> B = y.right;
		y.right = x;
		x.left = B;
		return x;
	}

	private List<Node<T>> inOrder(Node<T> node) {
		List<Node<T>> list = new ArrayList<>();
		inOrder(node, list);
		return list;
	}
	
	private void inOrder(Node<T> node, List<Node<T>> nodes) {
		if (node.left != null)
			inOrder(node.left, nodes);
		
		nodes.add(node);
		
		if(node.right != null) 
			inOrder(node.right, nodes);
	}
	
	@Override
	public Iterator<Node<T>> iterator() {
		return inOrder(root).iterator();
	}
	
	public static class Node<T> {
		final int start;
		final int end;
		final T val;
		Node<T> left;
		Node<T> right;
		int height;
		
		public Node(int start, int end, T val) {
			this.start = start;
			this.end = end;
			this.val = val;
			updateHeight();
		}
		
		public void updateHeight() {
			this.height = height();
		}
		
		public boolean isBalanced() {
			return Math.abs(heightLeft() - heightRight()) <= 1;
		}
		
		public boolean isRightHeavy() {
			return heightRight() > heightLeft();
		}
		
		public boolean isLeftHeavy() {
			return heightLeft() > heightRight();
		}
		
		private int height() {
			int max;
			if (heightLeft() > heightRight())
				max = heightLeft();
			else
				max = heightRight();
			
			return max + 1;
		}
		
		private int heightLeft() {
			return left == null ? -1 : left.height;
		}
		
		private int heightRight() {
			return right == null ? -1 : right.height;
		}
		
		public Node<T> getLeft() {
			return left;
		}
		
		public Node<T> getRight() {
			return right;
		}
		
		@Override
		public String toString() {
			return String.format("[%d-%d]", start, end);
		}
	}

}
