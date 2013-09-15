package org.jgll.util.trie;

import java.util.ArrayList;
import java.util.List;


public class Node<T> {

	private List<Object> info;
	private List<Edge<T>> edges;
	
	public Node() {
		edges = new ArrayList<>();
		info = new ArrayList<>();
	}
	
	public int size() {
		return edges.size();
	}
	
	
	public List<Edge<T>> getEdges() {
		return edges;
	}
	
	
	public List<Object> getInfo() {
		return info;
	}
	
	public void addChild(Edge<T> edge) {
		edges.add(edge);
	}

	public void addInfo(Object o) {
		info.add(o);
	}

}
