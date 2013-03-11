package org.jgll.traversal;

import org.jgll.grammar.LastGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

/**
 * ModelBuilderVisitor builds a data model by visiting an SPPF and 
 * calling appropriate models to the provided {@link NodeListener} 
 * interface.
 * 
 * The meta-data uneeded for building models, stored in grammar slots,
 * is automatically retrieved and passed to the given node listener.
 * 
 * @author Ali Afroozeh
 * 
 * @see NodeListener
 *
 */

// TODO: check if the conversions are really type safe! 
@SuppressWarnings("unchecked")
public class ModelBuilderVisitor<T, U> extends DefaultSPPFVisitor<Object> {
	
	private NodeListener<T, U> listener;
	
	public ModelBuilderVisitor(NodeListener<T, U> listener) {
		this.listener = listener;
	}

	@Override
	public void visit(TerminalSymbolNode terminal, Object object) {
		if(!terminal.isVisited()) {
			terminal.setVisited(true);
			if(terminal.getMatchedChar() == TerminalSymbolNode.EPSILON) {
				terminal.setObject(Result.skip());
			} else {
				Result<U> result = listener.terminal(terminal.getMatchedChar(), terminal);
				terminal.setObject(result);
			}
		}		
	}

	@Override
	public void visit(NonterminalSymbolNode nonterminalSymbolNode, Object object) {
		removeIntermediateNode(nonterminalSymbolNode);
		
		if(!nonterminalSymbolNode.isVisited()) {
		
			nonterminalSymbolNode.setVisited(true);
			
			if(nonterminalSymbolNode.isAmbiguous()) {
				
				buildAmbiguityNode(nonterminalSymbolNode, object);
				
			} else {
				LastGrammarSlot slot = (LastGrammarSlot) nonterminalSymbolNode.getFirstPackedNodeGrammarSlot();
				listener.startNode((T) slot.getObject());
				visitChildren(nonterminalSymbolNode, object);
				Result<U> result = listener.endNode((T) slot.getObject(), (Iterable<U>) nonterminalSymbolNode.childrenValues(), nonterminalSymbolNode);
				nonterminalSymbolNode.setObject(result);
			}
		}
	}

	private void buildAmbiguityNode(NonterminalSymbolNode nonterminalSymbolNode, Object object) {
		int nPackedNodes = 0;
		
		for(SPPFNode child : nonterminalSymbolNode.getChildren()) {
			PackedNode packedNode = (PackedNode) child;
			LastGrammarSlot slot = (LastGrammarSlot) packedNode.getGrammarSlot();
			listener.startNode((T) slot.getObject());
			packedNode.accept(this, object);
			Result<U> result = listener.endNode((T) slot.getObject(), (Iterable<U>) packedNode.childrenValues(), packedNode);
			packedNode.setObject(result);
			if(result != Result.filter()) {
				nPackedNodes++;
			}
		}
		
		if(nPackedNodes > 1) {
			Result<U> result = listener.buildAmbiguityNode((Iterable<U>) nonterminalSymbolNode.childrenValues(), nonterminalSymbolNode);
			nonterminalSymbolNode.setObject(result);
		}
	}

	@Override
	public void visit(IntermediateNode node, Object object) {
		// Intermediate nodes should be removed when visiting their parents.
		throw new RuntimeException("Should not be here!");
	}

	@Override
	public void visit(PackedNode packedNode, Object object) {
		removeIntermediateNode(packedNode);
		if(!packedNode.isVisited()) {
			packedNode.setVisited(true);
			visitChildren(packedNode, object);
		}
	}

	@Override
	public void visit(ListSymbolNode node, Object object) {
		removeListSymbolNode(node);
		if(!node.isVisited()) {
			node.setVisited(true);
			if(node.isAmbiguous()) {
				buildAmbiguityNode(node, object);
			} else {
				LastGrammarSlot slot = (LastGrammarSlot) node.getFirstPackedNodeGrammarSlot();
				listener.startNode((T) slot.getObject());
				visitChildren(node, object);
				Result<U> result = listener.endNode((T) slot.getObject(), (Iterable<U>) node.childrenValues(), node);
				node.setObject(result);
			}
		}
	}
	
}
