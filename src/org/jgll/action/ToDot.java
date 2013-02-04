package org.jgll.action;

import static org.jgll.util.GraphVizUtil.*;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see VisitAction
 */
public class ToDot implements VisitAction {
	
	private StringBuilder sb;
	
	public ToDot(StringBuilder sb) {
		this.sb = sb;
	}
	
	@Override
	public void execute(SPPFNode node) {
		
		if (node instanceof NonterminalSymbolNode || node instanceof TerminalSymbolNode) {
			sb.append("\"" + node.getId() + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(node.toString())) + "\n");
			addEdgesToChildren(node);			
		}
		
		if (node instanceof IntermediateNode) {
			sb.append("\"" + node.getId() + "\"" + String.format(INTERMEDIATE_NODE, replaceWhiteSpace(node.toString())) + "\n");
			addEdgesToChildren(node);
			
		} else if (node instanceof PackedNode) {
			sb.append("\"" + node.getId() + "\"" + String.format(PACKED_NODE, replaceWhiteSpace(node.toString())) + "\n");
			addEdgesToChildren(node);
		}
	}
	
	/**
	 * Adds edges to the given node's children
	 * 
	 * @param node the parent node
	 */
	private void addEdgesToChildren(SPPFNode node) {
		for (SPPFNode child : node.getChildren()) {
			sb.append(EDGE + "\"" + node.getId() + "\"" + "->" + "{\"" + child.getId() + "\"}" + "\n");
		}
	}
	
	private String replaceWhiteSpace(String s) {
		return s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n").replace("\r", "\\\\r").replace("\"", "\\\"");
	}
}
