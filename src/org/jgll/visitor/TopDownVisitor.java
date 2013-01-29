package org.jgll.visitor;

import org.jgll.action.VisitAction;
import org.jgll.sppf.SPPFNode;

public class TopDownVisitor implements Visitor {

	@Override
	public void visit(SPPFNode node, VisitAction...visitActions) {

		for(VisitAction visitAction : visitActions) {
			visitAction.execute(node);
		}
		
		for (SPPFNode child : node.getChildren()) {
			visit(child, visitActions);
		}
	}
}