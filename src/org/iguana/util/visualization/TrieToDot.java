package org.iguana.util.visualization;

import java.util.HashMap;
import java.util.Map;

import org.iguana.util.trie.Edge;
import org.iguana.util.trie.Node;
import org.iguana.util.trie.Trie;


public class TrieToDot<T> {
	
	private static final String NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"\", fontsize=10];";
	public static final String EDGE = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7, label=\"%s\"];";

	private String string;
	
	private Map<Node<T>, Integer> ids;

	public TrieToDot(Trie<T> trie) {
		ids = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		toDot(trie.getRoot(), sb);
		string = sb.toString();
	}
	
	private void toDot(Node<T> node, StringBuilder sb) {
		sb.append("\"").append(getId(node)).append("\"").append(NODE).append("\n");
		
		for (Edge<T> edge : node.getEdges()) {
			sb.append(String.format(EDGE, edge.getLabel())).append("\"").append(getId(node)).append("\"").append(" -> ").append("{");
			sb.append(getId(edge.getDestination()));
			sb.append("}").append("\n");
			toDot(edge.getDestination(), sb);
		}
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	
	public String getId(Node<T> node) {
		Integer id = ids.get(node);
		
		if (id == null) {
			id = ids.size() + 1;
			ids.put(node, id);
		}
		
		return id + "";
	}
}
