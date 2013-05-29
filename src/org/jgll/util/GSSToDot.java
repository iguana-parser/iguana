package org.jgll.util;

import static org.jgll.util.GraphVizUtil.GSS_EDGE;
import static org.jgll.util.GraphVizUtil.GSS_NODE;

import java.util.Collection;

import org.jgll.parser.GSSEdge;
import org.jgll.parser.GSSNode;

public class GSSToDot extends ToDot {
	
	private StringBuilder sb = new StringBuilder();
	
	public void execute(Collection<GSSNode> set) {
		for(GSSNode node : set) {
			
			sb.append("\"" + getId(node) + "\"" + String.format(GSS_NODE, node.toString()) + "\n");
			
			for(GSSEdge edge : node.getEdges()) {
				sb.append(String.format(GSS_EDGE, getId(edge.getSppfNode())) + "\"" + getId(node) + "\"" + "->" + "{\"" + getId(edge.getDestination()) + "\"}" + "\n");
			}
		}
	}

	private String getId(GSSNode node) {
		return node.getLabel() + "" + node.getIndex();
	}
	
	public String getString() {
		return sb.toString();
	}
	
}
