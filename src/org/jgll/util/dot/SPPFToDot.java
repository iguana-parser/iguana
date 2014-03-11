package org.jgll.util.dot;

import static org.jgll.util.dot.GraphVizUtil.EDGE;
import static org.jgll.util.dot.GraphVizUtil.INTERMEDIATE_NODE;
import static org.jgll.util.dot.GraphVizUtil.PACKED_NODE;
import static org.jgll.util.dot.GraphVizUtil.SYMBOL_NODE;

import org.jgll.grammar.Grammar;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.traversal.SPPFVisitorUtil;
import org.jgll.util.CollectionsUtil;
import org.jgll.util.Input;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class SPPFToDot extends ToDot implements SPPFVisitor  {
	
	private final boolean showPackedNodeLabel;
	
	protected StringBuilder sb;

	protected Input input;

	protected Grammar grammar;
	
	public SPPFToDot(Grammar grammar, Input input) {
		this(input, false);
		this.grammar = grammar;
	}
	
	public SPPFToDot(Input input, boolean showPackedNodeLabel) {
		this.input = input;
		this.showPackedNodeLabel = showPackedNodeLabel;
		this.sb = new StringBuilder();
	}

	@Override
	public void visit(TokenSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			String matchedInput = input.subString(node.getLeftExtent(), node.getRightExtent());
			String label = grammar.getRegularExpressionById(node.getId()).getName() + ": \"" + matchedInput + "\"";
			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
		}
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			String label = grammar.getNonterminalById(node.getId()).getName();
			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
			addEdgesToChildren(node);
			
			SPPFVisitorUtil.visitChildren(node, this);
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
	
			String label = CollectionsUtil.listToString(grammar.getIntermediateNodeSequence(node.getId()));
			sb.append("\"" + getId(node) + "\"" + String.format(INTERMEDIATE_NODE, replaceWhiteSpace(label)) + "\n");
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
		visit((NonterminalSymbolNode) node);
	}
	
	public String getString() {
		return sb.toString();
	}

}