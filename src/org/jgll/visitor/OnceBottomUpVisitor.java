package org.jgll.visitor;

import org.jgll.action.VisitAction;
import org.jgll.sppf.SPPFNode;

public class OnceBottomUpVisitor implements Visitor {

	@Override
	public void visit(SPPFNode node, VisitAction...visitActions) {
			
		if(node.isVisited()) {
			return;
		} else {
			node.setVisited();
			
			for(SPPFNode child : node.getChildren()) {
				visit(child, visitActions);
			}

			for(VisitAction visitAction : visitActions) {
				visitAction.execute(node);
			}
		}
		
	}
}
