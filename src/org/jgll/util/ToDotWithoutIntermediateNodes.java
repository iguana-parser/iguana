package org.jgll.util;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.SPPFNode;
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
	
	/**
	 * Don't draw the intermediate node or the links to its children.
	 * The links will be drawn from the parent of the intermediate node.
	 */
	public void visit(IntermediateNode node) {
		if(node.isVisited()) {
			return;
		}
		node.setVisited();

		visitChildren(node);
	}
	
	@Override
	protected void addEdgeToChild(SPPFNode parent, SPPFNode child) {
		if(child instanceof IntermediateNode) {
			for(SPPFNode childOfIntermediate : child.getChildren()) {
				addEdgeToChild(parent, childOfIntermediate);
			}
		} else {
			super.addEdgeToChild(parent, child);
		}
	}
	
}
