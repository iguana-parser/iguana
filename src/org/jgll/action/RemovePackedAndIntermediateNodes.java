package org.jgll.action;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.Modifiable;
import org.jgll.sppf.SPPFNode;

public class RemovePackedAndIntermediateNodes implements VisitAction {
	
	@Override
	public void execute(SPPFNode node) {
		
		if(!(node instanceof Modifiable)) {
			return;
		}
		
		
		Modifiable m = (Modifiable) node;
		
		if(m.sizeChildren() == 0) {
			System.out.println("Dafuq!?");
		}
		
		if (m.firstChild() instanceof IntermediateNode) {
						
			IntermediateNode intermediateNode = (IntermediateNode) m.firstChild();
			
			if(!intermediateNode.isAmbiguous()) {
				m.replaceByChildren(intermediateNode);
			}
		}
	}
	
}