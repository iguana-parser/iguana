package org.jgll.util.trie;



public class Edge {
	
	private String label;
	
	private Node destination;
	
	public Edge(String label, Node getDestination) {
		this.label = label;
		this.destination = getDestination;
	}
		
	public String getLabel() {
		return label;
	}
	
	public Node getDestination() {
		return destination;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
}
