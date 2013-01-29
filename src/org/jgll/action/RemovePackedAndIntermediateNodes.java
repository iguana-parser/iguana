package org.jgll.action;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.SPPFNode;

public class RemovePackedAndIntermediateNodes implements VisitAction {
	
	@Override
	public void execute(SPPFNode node) {
		
		if(node.sizeChildren() == 0) {
			return;
		}
		
		if (node.firstChild() instanceof IntermediateNode) {
						
			IntermediateNode intermediateNode = (IntermediateNode) node.firstChild();
			
			if(!intermediateNode.isAmbiguous()) {
				node.replaceByChildren(intermediateNode);
			}
		}
	}
	
}