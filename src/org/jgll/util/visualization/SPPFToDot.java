package org.jgll.util.visualization;

import static org.jgll.util.visualization.GraphVizUtil.*;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalNode;
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
	
	protected Set<NonPackedNode> visited = new HashSet<>();

	public SPPFToDot(GrammarRegistry registry, Input input) {
		this(input, registry, false);
	}
	
	public SPPFToDot(Input input, GrammarRegistry registry, boolean showPackedNodeLabel) {
		super(registry);
		this.input = input;
		this.showPackedNodeLabel = showPackedNodeLabel;
		this.sb = new StringBuilder();
	}

	@Override
	public void visit(TerminalNode node) {
		
		if(!visited.contains(node)) {
			visited.add(node);
			String matchedInput = input.subString(node.getLeftExtent(), node.getRightExtent());
			String label = String.format("(%s, %d, %d): \"%s\"", node.getGrammarSlot(), node.getLeftExtent(), node.getRightExtent(), matchedInput);
			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
		}
	}

	@Override
	public void visit(NonterminalNode node) {
		if(!visited.contains(node)) {
			visited.add(node);
			
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
		if(!visited.contains(node)) {
			visited.add(node);
			
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
		if(showPackedNodeLabel) {
			sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, replaceWhiteSpace(node.toString())) + "\n");
		} else {
			sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, "") + "\n");
		}
		addEdgesToChildren(node);
		
		SPPFVisitorUtil.visitChildren(node, this);
	}
	
	protected void addEdgesToChildren(SPPFNode node) {
		for (SPPFNode child : node.getChildren()) {
			addEdgeToChild(node, child);
		}
	}
	
	protected void addEdgeToChild(SPPFNode parentNode, SPPFNode childNode) {
		
		if (childNode instanceof PackedNode) {
			if(!((PackedNode) childNode).getParent().equals(parentNode)) {
				System.out.println("WTF?!");
			}
		}
		
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