package org.jgll.visitor;

import org.jgll.action.VisitAction;
import org.jgll.sppf.SPPFNode;

public class OnceInnerMostVisitor implements Visitor {

	@Override
	public void visit(SPPFNode node, VisitAction...visitActions) {

		if (node.isVisited()) {
			return;
		} else {

			for(SPPFNode child : node.getChildren()) {
				visit(child, visitActions);
			}

			node.setVisited();

			while (true) {
				try {
					
					for(VisitAction visitAction : visitActions) {
						visitAction.execute(node);						
					}
					
				} catch (Exception e) {
					break;
				}
			}
		}
	}
}
