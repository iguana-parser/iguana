package org.jgll.action;

import org.jgll.sppf.SPPFNode;


public class ClearVisitedNode implements VisitAction {

	@Override
	public void execute(SPPFNode node) {
		node.clearVisited();
	}
}
