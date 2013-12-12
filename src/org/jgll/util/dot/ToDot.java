package org.jgll.util.dot;

import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.sppf.TokenSymbolNode;

public abstract class ToDot {
	
	protected String getId(SPPFNode node) {
		
		if(node instanceof TerminalSymbolNode) {
			return getId((TerminalSymbolNode) node);
		} 
		else if(node instanceof NonPackedNode) {
			return getId((NonPackedNode) node);
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
	
	protected String getId(TerminalSymbolNode t) {
		return "t" + t.getMatchedChar() + "," + t.getLeftExtent() + "," + t.getRightExtent();
	}
	
	protected String getId(TokenSymbolNode t) {
		return "token_" + t.getTokenID() + "," + t.getLeftExtent() + "," + t.getRightExtent();
	}
	
	protected String getId(NonPackedNode n) {
		return n.getGrammarSlot().getId() + "," + n.getLeftExtent() + "," + n.getRightExtent();
	}
	
	protected String getId(PackedNode p) {
		return getId((NonPackedNode)p.getParent()) + "," + p.getGrammarSlot().getId() + "," + p.getPivot();
	}

}
