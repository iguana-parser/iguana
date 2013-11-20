package org.jgll.util.dot;

import static org.jgll.util.dot.GraphVizUtil.GSS_EDGE;
import static org.jgll.util.dot.GraphVizUtil.GSS_NODE;

import org.jgll.parser.GSSNode;
import org.jgll.sppf.SPPFNode;

public class GSSToDot extends ToDot {
	
	private StringBuilder sb = new StringBuilder();
	
	public void execute(Iterable<GSSNode> set) {
		for(GSSNode gssNode : set) {
			
			sb.append("\"" + getId(gssNode) + "\"" + String.format(GSS_NODE, gssNode.toString()) + "\n");
			
			for(GSSNode dest : gssNode.getChildren()) {
				for(SPPFNode sppfNode : gssNode.getNodesForChild(dest)) {
					sb.append(String.format(GSS_EDGE, getId(sppfNode)) + "\"" + getId(gssNode) + "\"" + "->" + "{\"" + getId(dest) + "\"}" + "\n");					
				}
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
