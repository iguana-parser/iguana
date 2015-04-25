package org.jgll.util.trie;



public class Edge<T> {
	
	private T label;
	
	private Node<T> destination;
	
	public Edge(T label, Node<T> destination) {
		
		if(label == null) throw new IllegalArgumentException("Label cannot be null.");
		if(destination == null) throw new IllegalArgumentException("Destination cannot be null.");
		
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
	
	@Override
	public int hashCode() {
		return label.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == this) return true;
		
		if(!(obj instanceof Edge)) return false;
		
		@SuppressWarnings("unchecked")
		Edge<T> other = (Edge<T>) obj;

		return label.equals(other.label);
	}
	
}
