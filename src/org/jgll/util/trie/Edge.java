package org.jgll.util.trie;



public class Edge<T> {
	
	private T label;
	
	private Node<T> destination;
	
	public Edge(T label, Node<T> destination) {
		this.label = label;
		this.destination = destination;
	}
		
	public T getLabel() {
		return label;
	}
	
	public Node<T> getDestination() {
		return destination;
	}
	
	@Override
	public String toString() {
		return label.toString();
	}
	
}
