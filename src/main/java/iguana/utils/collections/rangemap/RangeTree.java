/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package iguana.utils.collections.rangemap;

import iguana.utils.collections.rangemap.Range;
import iguana.utils.collections.rangemap.RangeTree.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

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

	public void insert(Range range, T val) {
		insert(range, root, new Node<T>(range.getStart(), range.getEnd(), val));
	}
	
	private void insert(Range range, Node<T> parent, Node<T> newNode) {
		
		if (root == null) {
			root = newNode;
			countNodes++;
			return;
		}
		
		if (range.getStart() < parent.start) {
			if (parent.left == null) {
				addToLeft(parent, newNode);
				updateHeight(newNode);
				if (parent.parent != null) 
					balance(parent.parent, parent);
			} else {
				insert(range, parent.left, newNode);
			}
		} 
		else if (range.getEnd() > parent.end) {
			if (parent.right == null) {
				addToRight(parent, newNode);
				updateHeight(newNode);
				if (parent.parent != null)
					balance(parent.parent, parent);
			} else {
				insert(range, parent.right, newNode);					
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
	 
	private void updateHeight(Node<T> node) {
		while (node != null) {
			node.updateHeight();
			node = node.parent;
		}
	}

	/**
	 * x         x           x      x
	 *  \         \         /      /
	 *   y         y       y      y
	 *    \       /       /        \
	 *     z     z       z          z
	 * 
	 */
	private void balance(Node<T> x, Node<T> y) {
		
		if (!x.isBalanced()) {
			if (x.right != null && x.right == y) {
				if (y.isRightHeavy()) {
					leftRotate(x);
				} 
				else if (y.isLeftHeavy()) {
					rightRotate(y);
					leftRotate(x);				
				}
			} 
			
			if (x.left != null & x.left == y) {
				if (y.isLeftHeavy()) {
					rightRotate(x);
				} 
				else if (y.isRightHeavy()){
					leftRotate(y);
					rightRotate(x);
				}
			}
		}
		
		if (x.parent != null)
			balance(x.parent, x);
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
		
		updateHeight(x);
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

		updateHeight(x);
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
