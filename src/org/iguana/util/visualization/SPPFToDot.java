package org.iguana.util.visualization;

import static org.iguana.util.visualization.GraphVizUtil.*;

import java.util.HashSet;
import java.util.Set;

import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.traversal.SPPFVisitor;
import org.iguana.traversal.SPPFVisitorUtil;
import org.iguana.util.Input;

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

	public SPPFToDot(Input input) {
		this(input, false);
	}
	
	public SPPFToDot(Input input, boolean showPackedNodeLabel) {
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

	public String getString() {
		return sb.toString();
	}

}