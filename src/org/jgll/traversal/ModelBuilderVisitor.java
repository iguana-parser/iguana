package org.jgll.traversal;

import org.jgll.grammar.LastGrammarSlot;
import org.jgll.sppf.IntermediateNode;
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
@SuppressWarnings("unchecked")
public class ModelBuilderVisitor<T> extends DefaultSPPFVisitor {
	
	private NodeListener<T> listener;
	
	public ModelBuilderVisitor(NodeListener<T> listener) {
		this.listener = listener;
	}

	@Override
	public void visit(TerminalSymbolNode terminal) {
		if(!terminal.isVisited()) {
			terminal.setVisited(true);
			if(terminal.getMatchedChar() != TerminalSymbolNode.EPSILON) {
				Object result = listener.terminal(terminal.getMatchedChar());
				terminal.setObject(result);
			}
		}		
	}

	@Override
	public void visit(NonterminalSymbolNode nonterminalSymbolNode) {
		removeIntermediateNode(nonterminalSymbolNode);
		
		if(!nonterminalSymbolNode.isVisited()) {
		
			nonterminalSymbolNode.setVisited(true);
			
			if(nonterminalSymbolNode.isAmbiguous()) {
				
				for(SPPFNode child : nonterminalSymbolNode) {
					PackedNode packedNode = (PackedNode) child;
					LastGrammarSlot slot = (LastGrammarSlot) packedNode.getGrammarSlot();
					listener.startNode((T) slot.getObject());
					packedNode.accept(this);
					Object result = listener.endNode((T) slot.getObject(), (Iterable<T>) packedNode.childrenValues());
					packedNode.setObject(result);
				}
				
				listener.buildAmbiguityNode((Iterable<T>) nonterminalSymbolNode.childrenValues());
				
			} else {
				LastGrammarSlot slot = (LastGrammarSlot) nonterminalSymbolNode.getFirstPackedNodeGrammarSlot();
				listener.startNode((T) slot.getObject());
				visitChildren(nonterminalSymbolNode);
				Object result = listener.endNode((T) slot.getObject(), (Iterable<T>) nonterminalSymbolNode.childrenValues());
				nonterminalSymbolNode.setObject(result);
			}
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		throw new RuntimeException("Should not be here!");
	}

	@Override
	public void visit(PackedNode packedNode) {
		removeIntermediateNode(packedNode);
		if(!packedNode.isVisited()) {
			packedNode.setVisited(true);
			visitChildren(packedNode);
		}
	}
	
}
