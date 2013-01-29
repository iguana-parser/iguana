package org.jgll.visitor;

import org.jgll.action.VisitAction;
import org.jgll.sppf.SPPFNode;

public class InnerMostVisitor implements Visitor {

	@Override
	public void visit(SPPFNode node, VisitAction...visitActions) {

		for(SPPFNode child : node.getChildren()) {
			visit(child, visitActions);
		}

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
