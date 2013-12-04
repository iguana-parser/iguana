package org.jgll.util.trie;

import java.util.Iterator;

public class Trie<T> {

	private Node<T> root;
	private ExternalEqual<T> externalEquals;
	
	public Trie(ExternalEqual<T> stringizer) {
		this.externalEquals = stringizer;
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
	
	private Node<T> add(Node<T> node, T label) {
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
			if(externalEquals.isEqual(edge.getLabel(), label)) {
				return edge.getDestination();
			}
		}
		return null;
	}
	
	public void add(Iterable<T> labels) {
	
		Node<T> node = root;
		
		Iterator<T> it = labels.iterator();
		
		while(it.hasNext()) {
			T label = it.next();
			node = add(node, label);
		}
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
