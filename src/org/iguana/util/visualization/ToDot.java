package org.jgll.util.visualization;

import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;

public abstract class ToDot {
	
	protected String getId(SPPFNode node) {
		
		if (node instanceof NonPackedNode) {
			NonPackedNode nonPackedNode = (NonPackedNode) node;
			return node.getGrammarSlot().getId() + "_" + nonPackedNode.getLeftExtent() + "_" + nonPackedNode.getRightExtent();
		} else {			
			PackedNode packedNode = (PackedNode) node;
			return  getId(packedNode.getParent())  + "_" + packedNode.getGrammarSlot().getId() + "_" + packedNode.getPivot();			
		}
	}
}
