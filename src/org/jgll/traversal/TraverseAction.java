package org.jgll.traversal;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.LastGrammarSlot;
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
		TerminalSymbolNode terminal = (TerminalSymbolNode) node;
		if(node.isVisited()) {
			listener.terminal(terminal.getMatchedChar(), node.getObject());
			return;
		}
		node.setVisited(true);
		if(terminal.getMatchedChar() != TerminalSymbolNode.EPSILON) {
			Object result = listener.terminal(terminal.getMatchedChar(), null);
			node.setObject(result);
		}
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		
		NonterminalSymbolNode nonterminalSymbolNode = (NonterminalSymbolNode) node;
		
		if(node.isVisited()) {
			LastGrammarSlot slot = (LastGrammarSlot) nonterminalSymbolNode.getFirstPackedNodeGrammarSlot();
			listener.startNode(slot.getObject());
			listener.endNode(slot.getObject(), nonterminalSymbolNode.getObject());
			return;
		}
		
		node.setVisited(true);
		
		if(nonterminalSymbolNode.isAmbiguous()) {
			
			List<Object> list = new ArrayList<>();
			
			for(SPPFNode child : nonterminalSymbolNode.getChildren()) {
				PackedNode packedNode = (PackedNode) child;
				LastGrammarSlot slot = (LastGrammarSlot) packedNode.getGrammarSlot();
				listener.startNode(slot.getObject());
				packedNode.accept(this);
				Object result = listener.endNode(slot.getObject(), null);
				packedNode.setObject(result);
				list.add(packedNode);
			}
			
			listener.buildAmbiguityNode(list);
			
		} else {
			LastGrammarSlot slot = (LastGrammarSlot) nonterminalSymbolNode.getFirstPackedNodeGrammarSlot();
			listener.startNode(slot.getObject());
			for(SPPFNode child : node.getChildren()) {
				child.accept(this);
			}
			Object result = listener.endNode(slot.getObject(), null);
			nonterminalSymbolNode.setObject(result);
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		if(node.isVisited()) {
			return;
		}
		node.setVisited(true);
		for(SPPFNode child : node.getChildren()) {
			child.accept(this);
		}
	}

	@Override
	public void visit(PackedNode node) {
		if(node.isVisited()) {
			return;
		}
		node.setVisited(true);
		for(SPPFNode child : node.getChildren()) {
			child.accept(this);
		}		
	}
	
}
