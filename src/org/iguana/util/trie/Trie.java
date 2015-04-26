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

package org.iguana.util.trie;

public class Trie<T> {

	private Node<T> root;
	
	public Trie() {
		this.root = new Node<>();
	}
	
	public void add(T label) {
		add(root, label);
	}
	
	public Node<T> get(Iterable<T> prefix) {
		Node<T> node = root;
		
		for(T label : prefix) {
			node = getNodeWithEdgeLabel(node, label);
			if(node == null) {
				return null;
			}
		}
		
		return node;
	}
	
	public void addToRoot(Iterable<T> labels) {
		Node<T> node = root;
		for (T label : labels) {
			node = add(node, label);
		}
	}
	
	public Node<T> add(Node<T> node, T label) {
		if(node.size() == 0) {
			return insert(node, label);
		}
		
		Node<T> dest = getNodeWithEdgeLabel(node, label);
		if(dest == null) {
			return insert(node, label);
		} else {
			return dest;
		}
	}
	
	private Node<T> getNodeWithEdgeLabel(Node<T> node, T label) {
		for(Edge<T> edge : node.getEdges()) {
			if(edge.getLabel().equals(label)) {
				return edge.getDestination();
			}
		}
		return null;
	}
		
	private Node<T> insert(Node<T> node, T label) {
		Node<T> newNode = new Node<>();
		node.addChild(new Edge<T>(label, newNode));
		return newNode;
	}
	
	public Node<T> getRoot() {
		return root;
	}
	
}
