package org.jgll.traversal;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

public class TraverseAction implements VisitAction {
	
	private SPPFListener listener;
	
	public TraverseAction(SPPFListener listener) {
		this.listener = listener;
	}

	@Override
	public void visit(TerminalSymbolNode node) {
		if(node.isVisited()) {
			return;
		}
		node.setVisited();
		TerminalSymbolNode terminal = (TerminalSymbolNode) node;
		listener.terminal(terminal.getMatchedChar());
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		if(node.isVisited()) {
			return;
		}
		node.setVisited();
		NonterminalSymbolNode nonterminalSymbolNode = (NonterminalSymbolNode) node;
		if(nonterminalSymbolNode.isAmbiguous()) {
			listener.startAmbiguityNode();
			
			for(SPPFNode packedNode : nonterminalSymbolNode.getChildren()) {
				listener.startPackedNode();
				packedNode.accept(this);
				listener.endPackedNode();
			}
			
			listener.endAmbiguityNode();
		} else {
			listener.startNode(nonterminalSymbolNode.getGrammarSlot());
			for(SPPFNode child : node.getChildren()) {
				child.accept(this);
			}
			listener.endNode(nonterminalSymbolNode.getGrammarSlot());
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		if(node.isVisited()) {
			return;
		}
		node.setVisited();
		for(SPPFNode child : node.getChildren()) {
			child.accept(this);
		}
	}

	@Override
	public void visit(PackedNode node) {
		if(node.isVisited()) {
			return;
		}
		node.setVisited();
		for(SPPFNode child : node.getChildren()) {
			child.accept(this);
		}		
	}
	
}
