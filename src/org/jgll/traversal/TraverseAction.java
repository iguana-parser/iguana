package org.jgll.traversal;

import java.util.ArrayList;
import java.util.List;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

public class TraverseAction extends DefaultSPPFVisitor {
	
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
		if(terminal.getMatchedChar() != TerminalSymbolNode.EPSILON) {
			listener.terminal(terminal.getMatchedChar());
		}
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		if(node.isVisited()) {
			return;
		}
		node.setVisited();
		NonterminalSymbolNode nonterminalSymbolNode = (NonterminalSymbolNode) node;
		if(nonterminalSymbolNode.isAmbiguous()) {
			
			List<Object> list = new ArrayList<>();
			
			for(SPPFNode child : nonterminalSymbolNode.getChildren()) {
				PackedNode packedNode = (PackedNode) child;
				listener.startNode(packedNode.getGrammarSlot());
				packedNode.accept(this);
				Object o = listener.endNode(packedNode.getGrammarSlot());
				if(o != null) {
					list.add(o);
				}
			}
			
			listener.buildAmbiguityNode(list);
		} else {
			listener.startNode(nonterminalSymbolNode.getFirstPackedNodeGrammarSlot());
			for(SPPFNode child : node.getChildren()) {
				child.accept(this);
			}
			listener.endNode(nonterminalSymbolNode.getFirstPackedNodeGrammarSlot());
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
