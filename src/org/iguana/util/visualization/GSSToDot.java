package org.iguana.util.visualization;

import static org.iguana.util.visualization.GraphVizUtil.*;

import org.iguana.parser.gss.GSSEdge;
import org.iguana.parser.gss.GSSNode;

public class GSSToDot extends ToDot {
	
	private StringBuilder sb = new StringBuilder();
	
	public void execute(Iterable<GSSNode> set) {
		
		for(GSSNode gssNode : set) {
			
			sb.append("\"" + getId(gssNode) + "\"" + String.format(GSS_NODE, gssNode.toString()) + "\n");
			
			for(GSSEdge edge : gssNode.getGSSEdges()) {
				String label = edge.getReturnSlot() == null ? "" : String.format(GSS_EDGE, edge.getReturnSlot()); 
				sb.append(label + "\"" + getId(gssNode) + "\"" + "->" + "{\"" + getId(edge.getDestination()) + "\"}" + "\n");				
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
