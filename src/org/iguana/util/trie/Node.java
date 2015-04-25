package org.iguana.util.trie;

import java.util.ArrayList;
import java.util.List;


public class Node<T> {

	private List<Edge<T>> edges;
	
	public Node() {
		edges = new ArrayList<>();
	}
	
	public int size() {
		return edges.size();
	}
	
	
	public List<Edge<T>> getEdges() {
		return edges;
	}
	
	
	public void addChild(Edge<T> edge) {
		edges.add(edge);
	}

}
