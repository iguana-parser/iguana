package org.jgll.util.dot;

import static org.jgll.util.dot.GraphVizUtil.GSS_EDGE;
import static org.jgll.util.dot.GraphVizUtil.GSS_NODE;

import org.jgll.parser.GSSEdge;
import org.jgll.parser.GSSNode;

public class GSSToDot extends ToDot {
	
	private StringBuilder sb = new StringBuilder();
	
	public void execute(Iterable<GSSNode> set) {
		for(GSSNode node : set) {
			
			sb.append("\"" + getId(node) + "\"" + String.format(GSS_NODE, node.toString()) + "\n");
			
			for(GSSEdge edge : node.getEdges()) {
				sb.append(String.format(GSS_EDGE, getId(edge.getSppfNode())) + "\"" + getId(node) + "\"" + "->" + "{\"" + getId(edge.getDestination()) + "\"}" + "\n");
			}
		}
	}

	private String getId(GSSNode node) {
		return node.getGrammarSlot() + "" + node.getInputIndex();
	}
	
	public String getString() {
		return sb.toString();
	}
	
}
