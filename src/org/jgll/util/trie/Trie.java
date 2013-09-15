package org.jgll.util.trie;

import java.util.Iterator;

public class Trie {

	private Node root;
	
	public Trie() {
		root = new Node();
	}
	
	public void add(String label) {
		add(root, label, null);
	}
	
	public Node get(Iterable<String> prefix) {
		Node node = root;
		
		for(String label : prefix) {
			node = getNodeWithEdgeLabel(node, label);
			if(node == null) {
				return null;
			}
		}
		
		return node;
	}
	
	private Node add(Node node, String label, Object object) {
		if(node.size() == 0) {
			return insert(node, label);
		}
		
		Node dest = getNodeWithEdgeLabel(node, label);
		if(dest == null) {
			return insert(node, label);
		} else {
			node.addInfo(object);
			return dest;
		}
	}
	
	private Node getNodeWithEdgeLabel(Node node, String label) {
		for(Edge edge : node.getEdges()) {
			if(edge.getLabel().equals(label)) {
				return edge.getDestination();
			}
		}
		return null;
	}
	
	public void add(Iterable<String> labels) {
		add(labels, null);
	}
	
	public void add(Iterable<String> labels, Object object) {
	
		Node node = root;
		
		Iterator<String> it = labels.iterator();
		
		while(it.hasNext()) {
			String label = it.next();
			node = add(node, label, node);
		}
	}
	
	private Node insert(Node node, String label) {
		Node newNode = new Node();
		node.addChild(new Edge(label, newNode));
		return newNode;
	}
	
	
}
