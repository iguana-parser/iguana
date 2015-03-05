package org.jgll.util.visualization;

import static org.jgll.util.generator.GeneratorUtil.*;
import static org.jgll.util.visualization.GraphVizUtil.*;

import org.jgll.util.collections.IntRangeTree;
import org.jgll.util.collections.IntRangeTree.IntNode;
import org.jgll.util.collections.RangeTree;
import org.jgll.util.collections.RangeTree.Node;

public class RangeTreeToDot {
		
	public static <T> String toDot(RangeTree<T> g) {
		StringBuilder sb = new StringBuilder();
		toDot(sb, g.getRoot());
		return sb.toString();
	}
	
	private static <T> void toDot(StringBuilder sb, Node<T> node) {
		sb.append("\"" + node.toString() + "\"" + String.format(NODE, escape(node.toString()) + "\n"));
		
		if (node.getLeft() != null) {
			sb.append(EDGE + "\"" + node.toString() + "\"" + "->" + "{\"" + node.getLeft().toString() + "\"}" + "\n");
			toDot(sb, node.getLeft());
		}
		
		if (node.getRight() != null) {
			sb.append(EDGE + "\"" + node.toString() + "\"" + "->" + "{\"" + node.getRight().toString() + "\"}" + "\n");
			toDot(sb, node.getRight());
		}
	}
	
	
	public static <T> String toDot(IntRangeTree g) {
		StringBuilder sb = new StringBuilder();
		toDot(sb, g.getRoot());
		return sb.toString();
	}
	
	private static <T> void toDot(StringBuilder sb, IntNode node) {
		sb.append("\"" + escape(node.toString()) + "\"" + String.format(NODE, escape(node.toString())) + "\n");
		
		if (node.getLeft() != null) {
			sb.append(EDGE + "\"" + escape(node.toString()) + "\"" + "->" + "{\"" + escape(node.getLeft().toString()) + "\"}" + "\n");
			toDot(sb, node.getLeft());
		}
		
		if (node.getRight() != null) {
			sb.append(EDGE + "\"" + escape(node.toString()) + "\"" + "->" + "{\"" + escape(node.getRight().toString()) + "\"}" + "\n");
			toDot(sb, node.getRight());
		}
	}

}
