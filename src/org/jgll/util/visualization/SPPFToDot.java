package org.jgll.util.visualization;

import static org.jgll.util.visualization.GraphVizUtil.*;

import org.jgll.grammar.GrammarGraph;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.traversal.SPPFVisitorUtil;
import org.jgll.util.Input;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 * 
 */
public class SPPFToDot extends ToDot implements SPPFVisitor  {
	
	protected final boolean showPackedNodeLabel;
	
	protected StringBuilder sb;

	protected Input input;

	public SPPFToDot(GrammarGraph grammar, Input input) {
		this(input, false);
	}
	
	public SPPFToDot(Input input, boolean showPackedNodeLabel) {
		this.input = input;
		this.showPackedNodeLabel = showPackedNodeLabel;
		this.sb = new StringBuilder();
	}

	@Override
	public void visit(TerminalSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			String matchedInput = input.subString(node.getLeftExtent(), node.getRightExtent());
			String label = String.format("(%s, %d, %d): \"%s\"", node.getGrammarSlot(), node.getLeftExtent(), node.getRightExtent(), matchedInput);
			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
		}
	}

	@Override
	public void visit(NonterminalNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			String label = String.format("(%s, %d, %d)", node.getGrammarSlot(), node.getLeftExtent(), node.getRightExtent());
			if (node.isAmbiguous()) {
				sb.append("\"" + getId(node) + "\"" + String.format(AMBIGUOUS_SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
			} else {
				sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");				
			}
			addEdgesToChildren(node);
			
			SPPFVisitorUtil.visitChildren(node, this);
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			String label = String.format("(%s, %d, %d)", node.getGrammarSlot(), node.getLeftExtent(), node.getRightExtent());
			if (node.isAmbiguous()) {
				sb.append("\"" + getId(node) + "\"" + String.format(AMBIGUOUS_INTERMEDIATE_NODE, replaceWhiteSpace(label)) + "\n");
			} else {
				sb.append("\"" + getId(node) + "\"" + String.format(INTERMEDIATE_NODE, replaceWhiteSpace(label)) + "\n");
			}
			addEdgesToChildren(node);
	
			SPPFVisitorUtil.visitChildren(node, this);
		}
	}

	@Override
	public void visit(PackedNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			if(showPackedNodeLabel) {
				sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, replaceWhiteSpace(node.toString())) + "\n");
			} else {
				sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, "") + "\n");
			}
			addEdgesToChildren(node);
			
			SPPFVisitorUtil.visitChildren(node, this);
		}
	}
	
	protected void addEdgesToChildren(SPPFNode node) {
		for (SPPFNode child : node.getChildren()) {
			addEdgeToChild(node, child);
		}
	}
	
	protected void addEdgeToChild(SPPFNode parentNode, SPPFNode childNode) {
		sb.append(EDGE + "\"" + getId(parentNode) + "\"" + "->" + "{\"" + getId(childNode) + "\"}" + "\n");
	}
	
	protected String replaceWhiteSpace(String s) {
		return s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n").replace("\r", "\\\\r").replace("\"", "\\\"");
	}

	@Override
	public void visit(ListSymbolNode node) {
		visit((NonterminalNode) node);
	}
	
	public String getString() {
		return sb.toString();
	}

}