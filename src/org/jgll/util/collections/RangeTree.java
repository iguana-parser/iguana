package org.jgll.util.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.util.collections.RangeTree.Node;

/**
 * 
 * An implementaion of AVL trees for fast range search
 * 
 * @author Ali Afroozeh
 *
 */
public class RangeTree<T> implements Iterable<Node<T>>{	

	private Node<T> root;
	
	private int countNodes;
	
	public Node<T> getRoot() {
		return root;
	}
	
	public int size() {
		return countNodes;
	}
	
	public boolean contains(int key) {
		return get(key) != null;
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
	
	public void insert(CharacterRange range) {
		insert(range, null);
	}
	
	public void insert(CharacterRange range, T val) {
		insert(range, val, root);
	}
	
	private void insert(CharacterRange range, T val, Node<T> node) {		
		Node<T> newNode = new Node<>(range.getStart(), range.getEnd(), val);
		
		if (root == null) {
			root = newNode;
			countNodes++;
			return;
		}
		
		if (range.getStart() < node.start) {
			if (node.left == null) {
				addToLeft(node, newNode);
				balance(node);
			} else {
				insert(range, val, node.left);
			}
		} 
		else if (range.getEnd() > node.end) {
			if (node.right == null) {
				addToRight(node, newNode);
				balance(node);
			} else {
				insert(range, val, node.right);					
			}
		}
	}
	
	private void addToLeft(Node<T> parent, Node<T> newNode) {
		countNodes++;
		parent.left = newNode;
		newNode.parent = parent;
	}
	
	private void addToRight(Node<T> parent, Node<T> newNode) {
		countNodes++;
		parent.right = newNode;
		newNode.parent = parent;
	}
	 
	private void balance(Node<T> node) {
		node.updateHeight();
		
		if (!node.isBalanced()) {
			if (node.right != null) {
				if (node.right.isRightHeavy()) {
					leftRotate(node);
				} 
				else if (node.right.isLeftHeavy()) {
					rightRotate(node.right);
					leftRotate(node);
				}
			}
			if (node.left != null) {
				if (node.left.isLeftHeavy()) {
					rightRotate(node);					
				} 
				else if (node.left.isRightHeavy()) {
					leftRotate(node.left);
					rightRotate(node);
				}
			}
		}
		
		if (node.parent != null) {
			balance(node.parent);
		}
	}

	/**
	 *      x                y
	 *     / \              / \
	 *    A   y     =>     x   C 
	 *       / \          / \
	 *      B   C        A   B
	 */
	private void leftRotate(Node<T> x) {
		Node<T> parent = x.parent;
		
		Node<T> y = x.right;
		Node<T> B = y.left;
		y.left = x;
		x.parent = y;
		x.right = B;
		if (B != null)
			B.parent = x;
		
		y.parent = parent;
		if (parent != null) {
			parent.replaceChild(x, y);
		} else {
			root = y;
		}
		
		x.updateHeight();
		y.updateHeight();
	}
	
	/**
	 *      x                y
	 *     / \              / \
	 *    y   C     =>     A   x 
	 *   / \                  / \
	 *  A   B                B   C
	 *      
	 */
	private void rightRotate(Node<T> x) {
		Node<T> parent = x.parent;
		
		Node<T> y = x.left;
		Node<T> B = y.right;
		y.right = x;
		x.parent = y;
		x.left = B;
		
		if (B != null)
			B.parent = x;

		y.parent = parent;
		if (parent != null) {
			parent.replaceChild(x, y);
		} else {
			root = y;
		}
		
		x.updateHeight();
		y.updateHeight();
	}
	
	public boolean isBalanced() {
		return inOrder(root).stream().allMatch(n -> n.isBalanced());
	}

	private List<Node<T>> inOrder(Node<T> node) {
		List<Node<T>> list = new ArrayList<>();
		inOrder(node, (n) -> list.add(n));
		return list;
	}
	
	private void inOrder(Node<T> node, Consumer<Node<T>> consumer) {
		if (node.left != null)
			inOrder(node.left, consumer);
		
		consumer.accept(node);
		
		if(node.right != null) 
			inOrder(node.right, consumer);
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
		
		Node<T> parent;
		
		public Node(int start, int end, T val) {
			this.start = start;
			this.end = end;
			this.val = val;
			updateHeight();
		}
		
		public void replaceChild(Node<T> child, Node<T> replacement) {
			// Fix the pointers of the parents
			if (left == child) {
				left = replacement;
			} else {
				right = replacement;
			}		
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
		
		public int getHeight() {
			return height;
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
