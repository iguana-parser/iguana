package org.jgll.util.dot;

import org.jgll.sppf.DummyNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

public abstract class ToDot {
	
	protected String getId(SPPFNode node) {
		
		if(node instanceof NonterminalSymbolNode) {
			return getId((NonterminalSymbolNode) node);
		}
		else if (node instanceof IntermediateNode) {
			return getId((IntermediateNode) node);
		}
		else if(node instanceof PackedNode) {
			return getId((PackedNode) node);
		}
		else if(node instanceof DummyNode) {
			return "-1";
		} 
		else if(node instanceof TokenSymbolNode) {
			return getId((TokenSymbolNode) node);
		}
		throw new RuntimeException("Node of type " +  node.getClass() + " could not be matched.");
	}
	
	protected String getId(TokenSymbolNode t) {
		return "token_" + t.getTokenID() + "," + t.getLeftExtent() + "," + t.getRightExtent();
	}
	
	protected String getId(NonterminalSymbolNode n) {
		return "nontermianl_" + n.getId() + "," + n.getLeftExtent() + "," + n.getRightExtent();
	}
	
	protected String getId(IntermediateNode n) {
		return "intermediate_" + n.getId() + "," + n.getLeftExtent() + "," + n.getRightExtent();
	}
	
	protected String getId(PackedNode p) {
		return "packed_" + getId((NonPackedNode)p.getParent()) + "," + p.getId() + "," + p.getPivot();
	}
	
}
