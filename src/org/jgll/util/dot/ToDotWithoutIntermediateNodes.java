package org.jgll.util.dot;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.traversal.SPPFVisitorUtil;
import org.jgll.util.Input;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class ToDotWithoutIntermediateNodes extends SPPFToDot {
	
	public ToDotWithoutIntermediateNodes(Input input) {
		super(input);
	}

	@Override
	public void visit(PackedNode node) {
		SPPFVisitorUtil.removeIntermediateNode(node);
		SPPFVisitorUtil.removeCollapsibleNode(node);
		super.visit(node);
	}
	
	@Override
	public void visit(NonterminalSymbolNode node) {
		SPPFVisitorUtil.removeIntermediateNode(node);
		SPPFVisitorUtil.removeCollapsibleNode(node);
		super.visit(node);
	}
}
