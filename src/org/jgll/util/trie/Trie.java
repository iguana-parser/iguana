package org.jgll.util.trie;

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
			if(edge.getLabel().equals(label)) {
				return edge.getDestination();
			}
		}
		return null;
	}
	
	public void addToRoot(Iterable<T> labels) {
		Node<T> node = root;
		for (T label : labels) {
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
