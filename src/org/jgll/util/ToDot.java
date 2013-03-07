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
import org.jgll.traversal.DefaultSPPFVisitor;
import org.jgll.traversal.SPPFVisitor;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class ToDot extends DefaultSPPFVisitor {
	
	protected StringBuilder sb;
	private final boolean showPackedNodeLabel;
	
	public ToDot(StringBuilder sb) {
		this(sb, false);
	}
	
	public ToDot(StringBuilder sb, boolean showPackedNodeLabel) {
		this.sb = sb;
		this.showPackedNodeLabel = showPackedNodeLabel;
	}

	@Override
	public void visit(TerminalSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			String label = node.toString();
			// Replace the Java-style unicode char for epsilon with the graphviz one
			label.replace("\u03B5", "&epsilon;");
			sb.append("\"" + node.getId() + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
			addEdgesToChildren(node);
		}
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			sb.append("\"" + node.getId() + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(node.toString())) + "\n");
			addEdgesToChildren(node);
			
			visitChildren(node);
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			sb.append("\"" + node.getId() + "\"" + String.format(INTERMEDIATE_NODE, replaceWhiteSpace(node.toString())) + "\n");
			addEdgesToChildren(node);
	
			visitChildren(node);
		}
	}

	@Override
	public void visit(PackedNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			if(showPackedNodeLabel) {
				sb.append("\"" + node.getId() + "\"" + String.format(PACKED_NODE, replaceWhiteSpace(node.toString())) + "\n");
			} else {
				sb.append("\"" + node.getId() + "\"" + String.format(PACKED_NODE, "") + "\n");
			}
			addEdgesToChildren(node);
			
			visitChildren(node);
		}
	}
	
	protected void addEdgesToChildren(SPPFNode node) {
		for (SPPFNode child : node.getChildren()) {
			addEdgeToChild(node, child);
		}
	}
	
	protected void addEdgeToChild(SPPFNode parent, SPPFNode child) {
		sb.append(EDGE + "\"" + parent.getId() + "\"" + "->" + "{\"" + child.getId() + "\"}" + "\n");
	}
	
	private String replaceWhiteSpace(String s) {
		return s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n").replace("\r", "\\\\r").replace("\"", "\\\"");
	}

	@Override
	public void visit(ListSymbolNode node) {
		visit((NonterminalSymbolNode)node);
	}

}
