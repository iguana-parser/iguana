package org.jgll.util.trie;

import java.util.ArrayList;
import java.util.List;


public class Node {

	private List<Object> info;
	private List<Edge> edges;
	
	public Node() {
		edges = new ArrayList<>();
		info = new ArrayList<>();
	}
	
	public int size() {
		return edges.size();
	}
	
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	
	public List<Object> getInfo() {
		return info;
	}
	
	public void addChild(Edge edge) {
		edges.add(edge);
	}

	public void addInfo(Object o) {
		info.add(o);
	}

}
