package org.jgll.visitor;

import org.jgll.action.VisitAction;
import org.jgll.sppf.SPPFNode;


public class OnceTopDownVisitor implements Visitor {
	

	@Override
	public void visit(SPPFNode node, VisitAction...visitActions) {
		
		if(node.isVisited()) {
			return;	
		} else {
			node.setVisited();
			
			for(VisitAction visitAction : visitActions) {
				visitAction.execute(node);
			}
						
			for(SPPFNode child : node.getChildren()) {
				visit(child, visitActions);
			}
		}
	}
}
