package org.jgll.util.dot;

import org.jgll.sppf.DummyNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

public abstract class ToDot {
	
	protected String getId(SPPFNode node) {
		
		if(node instanceof NonterminalSymbolNode) {
			return "nontermianl_" + getNodeId(node);
		}
		else if (node instanceof IntermediateNode) {
			return "intermediate_" + getNodeId(node);
		}
		else if(node instanceof PackedNode) {
			return "packed_" + getNodeId(((PackedNode) node).getParent()) + getNodeId(node);
		}
		else if(node instanceof DummyNode) {
			return "-1";
		} 
		else if(node instanceof TokenSymbolNode) {
			return "token_" + getNodeId(node);
		}
		throw new RuntimeException("Node of type " +  node.getClass() + " could not be matched.");
	}
	
	protected String getNodeId(SPPFNode node) {
		return node.getId() + "," + node.getLeftExtent() + "," + node.getRightExtent();
	}
	
}
