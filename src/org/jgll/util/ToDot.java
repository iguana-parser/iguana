package org.jgll.util;

import static org.jgll.util.GraphVizUtil.EDGE;
import static org.jgll.util.GraphVizUtil.INTERMEDIATE_NODE;
import static org.jgll.util.GraphVizUtil.PACKED_NODE;
import static org.jgll.util.GraphVizUtil.SYMBOL_NODE;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.traversal.SPPFVisitorUtil;
import org.jgll.traversal.SPPFVisitor;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class ToDot implements SPPFVisitor<StringBuilder> {
	
	private final boolean showPackedNodeLabel;
	
	public ToDot() {
		showPackedNodeLabel = false;
	}
	
	public ToDot(boolean showPackedNodeLabel) {
		this.showPackedNodeLabel = showPackedNodeLabel;
	}

	@Override
	public void visit(TerminalSymbolNode node, StringBuilder sb) {
		if(!node.isVisited()) {
			node.setVisited(true);
			String label = node.toString();
			// Replace the Java-style unicode char for epsilon with the graphviz one
			label.replace("\u03B5", "&epsilon;");
			sb.append("\"" + node.getId() + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
		}
	}

	@Override
	public void visit(NonterminalSymbolNode node, StringBuilder sb) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			sb.append("\"" + node.getId() + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(node.toString())) + "\n");
			addEdgesToChildren(node, sb);
			
			SPPFVisitorUtil.visitChildren(node, this, sb);
		}
	}

	@Override
	public void visit(IntermediateNode node, StringBuilder sb) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			sb.append("\"" + node.getId() + "\"" + String.format(INTERMEDIATE_NODE, replaceWhiteSpace(node.toString())) + "\n");
			addEdgesToChildren(node, sb);
	
			SPPFVisitorUtil.visitChildren(node, this, sb);
		}
	}

	@Override
	public void visit(PackedNode node, StringBuilder sb) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			if(showPackedNodeLabel) {
				sb.append("\"" + node.getId() + "\"" + String.format(PACKED_NODE, replaceWhiteSpace(node.toString())) + "\n");
			} else {
				sb.append("\"" + node.getId() + "\"" + String.format(PACKED_NODE, "") + "\n");
			}
			addEdgesToChildren(node, sb);
			
			SPPFVisitorUtil.visitChildren(node, this, sb);
		}
	}
	
	protected void addEdgesToChildren(SPPFNode node, StringBuilder sb) {
		for (SPPFNode child : node.getChildren()) {
			addEdgeToChild(node, child, sb);
		}
	}
	
	protected void addEdgeToChild(SPPFNode parent, SPPFNode child, StringBuilder sb) {
		sb.append(EDGE + "\"" + parent.getId() + "\"" + "->" + "{\"" + child.getId() + "\"}" + "\n");
	}
	
	protected String replaceWhiteSpace(String s) {
		return s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n").replace("\r", "\\\\r").replace("\"", "\\\"");
	}

	@Override
	public void visit(ListSymbolNode node, StringBuilder sb) {
		visit((NonterminalSymbolNode)node, sb);
	}

}
