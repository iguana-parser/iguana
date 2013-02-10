package org.jgll.util;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.traversal.SPPFVisitor;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class ToDotWithoutIntermediateNodes extends ToDot {

	public ToDotWithoutIntermediateNodes(StringBuilder sb) {
		super(sb);
	}
	
	@Override
	public void visit(PackedNode node) {
		removeIntermediateNode(node);
		super.visit(node);
	}
	
	@Override
	public void visit(NonterminalSymbolNode node) {
		removeIntermediateNode(node);
		super.visit(node);
	}
}
