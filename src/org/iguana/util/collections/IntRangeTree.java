/*
 * Copyright (c) 2015, CWI
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

package org.iguana.util.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.util.collections.IntRangeTree.IntNode;

/**
 * 
 * An implementaion of AVL trees for fast range search
 * that used integer keys.
 * 
 * @author Ali Afroozeh
 *
 */
public class IntRangeTree implements Iterable<IntNode>{	

	private IntNode root;
	
	private int countNodes;
	
	public IntNode getRoot() {
		return root;
	}
	
	public int size() {
		return countNodes;
	}
	
	public boolean contains(int key) {
		return get(key) != -1;
	}
	
	public boolean contains(CharacterRange range) {
		return getNode(range, root) != null;
	}
	
	private IntNode getNode(CharacterRange range, IntNode node) {
		if (node == null)
			return null;
		
		if (range.getStart() == node.start && range.getEnd() == node.end)
			return node;
		
		if (range.getStart() < node.start)
			return getNode(range, node.left);
		
		if (range.getEnd() > node.end)
			return getNode(range, node.right);
		
		throw new RuntimeException("Should not reach here!");
	}
	
	public int get(int key) {
		return get(key, root);
	}
	
	public int get(int key, IntNode node) {
		if (node == null)
			return -1;
		
		if (key < node.start) 
			return get(key, node.left);
		
		if (key > node.end)
			return get(key, node.right);
		
		return node.val;
	}
	
	public void insert(int key, int val) {
		insert(CharacterRange.in(key, key), val);
	}
	
	public void insert(CharacterRange range, int val) {
		insert(range, val, root, new IntNode(range.getStart(), range.getEnd(), val));
	}
	
	private void insert(CharacterRange range, int val, IntNode parent, IntNode newNode) {		
		
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
				insert(range, val, parent.left, newNode);
			}
		} 
		else if (range.getEnd() > parent.end) {
			if (parent.right == null) {
				addToRight(parent, newNode);
				updateHeight(newNode);
				if (parent.parent != null)
					balance(parent.parent, parent);
			} else {
				insert(range, val, parent.right, newNode);					
			}
		}
	}
	
	private void addToLeft(IntNode parent, IntNode newNode) {
		countNodes++;
		parent.left = newNode;
		newNode.parent = parent;
	}
	
	private void addToRight(IntNode parent, IntNode newNode) {
		countNodes++;
		parent.right = newNode;
		newNode.parent = parent;
	}
	
	private void updateHeight(IntNode node) {
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
	private void balance(IntNode x, IntNode y) {
		
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
	private void leftRotate(IntNode x) {
		IntNode parent = x.parent;
		
		IntNode y = x.right;
		IntNode B = y.left;
		y.left = x;
		x.parent = y;
		x.right = B;
		if (B != null)
			B.parent = x;
		
		y.parent = parent;
		
		if (parent != null)
			parent.replaceChild(x, y);
		else
			root = y;
		
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
	private void rightRotate(IntNode x) {
		IntNode parent = x.parent;
		
		IntNode y = x.left;
		IntNode B = y.right;
		y.right = x;
		x.parent = y;
		x.left = B;
		
		if (B != null)
			B.parent = x;

		y.parent = parent;
		if (parent != null) 
			parent.replaceChild(x, y);
		else 
			root = y;
		
		updateHeight(x);
	}
	
	public boolean isBalanced() {
		return inOrder(root).stream().allMatch(n -> n.isBalanced());
	}

	private List<IntNode> inOrder(IntNode node) {
		List<IntNode> list = new ArrayList<>();
		inOrder(node, (n) -> list.add(n));
		return list;
	}
	
	private void inOrder(IntNode node, Consumer<IntNode> consumer) {
		if (node.left != null)
			inOrder(node.left, consumer);
		
		consumer.accept(node);
		
		if(node.right != null) 
			inOrder(node.right, consumer);
	}
	
	@Override
	public Iterator<IntNode> iterator() {
		return inOrder(root).iterator();
	}
	
	public static class IntNode {
		final int start;
		final int end;
		final int val;
		IntNode left;
		IntNode right;
		int height;
		
		IntNode parent;
		
		public IntNode(int start, int end, int val) {
			this.start = start;
			this.end = end;
			this.val = val;
			updateHeight();
		}
		
		public void replaceChild(IntNode child, IntNode replacement) {
			// Fixes the pointers of the parents
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
		
		public IntNode getLeft() {
			return left;
		}
		
		public IntNode getRight() {
			return right;
		}
		
		@Override
		public String toString() {
			return String.format("%s-%s", Character.getName(start), Character.getName(end));
		}
	}

}
