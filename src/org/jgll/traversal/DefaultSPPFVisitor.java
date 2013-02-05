package org.jgll.traversal;

import org.jgll.sppf.SPPFNode;

public abstract class DefaultSPPFVisitor implements SPPFVisitor {

	protected void visitChildren(SPPFNode node) {
		for(SPPFNode child : node.getChildren()) {
			child.accept(this);
		}
	}
	
}
