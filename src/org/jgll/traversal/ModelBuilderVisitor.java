package org.jgll.traversal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbols.CharacterClass;
import org.jgll.sppf.CollapsibleNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.RegularListNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;

import static org.jgll.traversal.SPPFVisitorUtil.*;

/**
 * ModelBuilderVisitor builds a data model by visiting an SPPF and 
 * calling appropriate models to the provided {@link NodeListener} 
 * interface.
 * 
 * The meta-data needed for building models, stored in grammar slots,
 * is automatically retrieved and passed to the given node listener.
 * 
 * @author Ali Afroozeh
 * 
 * @see NodeListener
 *
 */

@SuppressWarnings("unchecked")
public class ModelBuilderVisitor<T, U> implements SPPFVisitor {
	
	private NodeListener<T, U> listener;
	private Input input;
	
	public ModelBuilderVisitor(Input input, NodeListener<T, U> listener) {
		this.input = input;
		this.listener = listener;
	}

	@Override
	public void visit(TerminalSymbolNode terminal) {
		if(!terminal.isVisited()) {
			terminal.setVisited(true);
			if(terminal.getMatchedChar() == TerminalSymbolNode.EPSILON) {
				terminal.setObject(Result.skip());
			} else {
				Result<U> result = listener.terminal(terminal.getMatchedChar(), input.getPositionInfo(terminal.getLeftExtent(), terminal.getRightExtent()));
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
				
				buildAmbiguityNode(nonterminalSymbolNode);
				
			// Lazy creation of the children of a keyword.
			} else if(nonterminalSymbolNode.isKeywordNode()) {
				HeadGrammarSlot head = (HeadGrammarSlot) nonterminalSymbolNode.getGrammarSlot();
				BodyGrammarSlot currentSlot = head.getAlternateAt(0).getFirstSlot();
				
				List<U> list = new ArrayList<>();
				int i = nonterminalSymbolNode.getLeftExtent();
				while(!(currentSlot instanceof LastGrammarSlot)) {
					CharacterClass terminal = (CharacterClass) ((TerminalGrammarSlot)currentSlot).getTerminal();
					assert terminal.getRanges().size() == 1;
					Result<U> result = listener.terminal(terminal.getRanges().get(0).getStart(), input.getPositionInfo(i, ++i));
					list.add(result.getObject());
					currentSlot = currentSlot.next();
				}

				LastGrammarSlot slot = (LastGrammarSlot) nonterminalSymbolNode.getFirstPackedNodeGrammarSlot();
				listener.startNode((T) slot.getObject());
				Result<U> result = listener.endNode((T) slot.getObject(), list, 
						input.getPositionInfo(nonterminalSymbolNode.getLeftExtent(), nonterminalSymbolNode.getRightExtent()));
				nonterminalSymbolNode.setObject(result);
				
			// Lazy creation of the children of regular nodes	
			} else if(nonterminalSymbolNode instanceof RegularListNode) {
				RegularListNode regularListNode = (RegularListNode) nonterminalSymbolNode;
				List<U> list = new ArrayList<>();
				for(int i = regularListNode.getLeftExtent(); i < regularListNode.getRightExtent(); i++) {
					Result<U> result = listener.terminal(input.charAt(i), input.getPositionInfo(i, i + 1));
					list.add(result.getObject());
				}
								
				LastGrammarSlot slot = (LastGrammarSlot) nonterminalSymbolNode.getFirstPackedNodeGrammarSlot();
				listener.startNode((T) slot.getObject());
				Result<U> result = listener.endNode((T) slot.getObject(), list, 
						input.getPositionInfo(nonterminalSymbolNode.getLeftExtent(), nonterminalSymbolNode.getRightExtent()));
				nonterminalSymbolNode.setObject(result);				

			} else {
				
				LastGrammarSlot slot = (LastGrammarSlot) nonterminalSymbolNode.getFirstPackedNodeGrammarSlot();
				
				Result<U> result;
				
				if(nonterminalSymbolNode.getLastChild() instanceof CollapsibleNode) {
					CollapsibleNode lastChild = (CollapsibleNode) nonterminalSymbolNode.getLastChild();
					Object object = getObject(lastChild);
					listener.startNode((T) object);
					visitChildren(nonterminalSymbolNode, this);
					removeCollapsibleNode(nonterminalSymbolNode);
					result = listener.endNode((T) object, getChildrenValues(nonterminalSymbolNode), 
								input.getPositionInfo(nonterminalSymbolNode.getLeftExtent(), nonterminalSymbolNode.getRightExtent()));
				} else {
					listener.startNode((T) slot.getObject());
					visitChildren(nonterminalSymbolNode, this);
					result = listener.endNode((T) slot.getObject(), getChildrenValues(nonterminalSymbolNode), 
								input.getPositionInfo(nonterminalSymbolNode.getLeftExtent(), nonterminalSymbolNode.getRightExtent()));
				}

				nonterminalSymbolNode.setObject(result);
			}
		}
	}
	
	// TODO: does not work for collapsible nodes which are ambiguous.
	// They should be lifted, similar to intermediate nodes.
	private Object getObject(CollapsibleNode node) {
		
		CollapsibleNode collapsibleNode = node;
		if(collapsibleNode.childrenCount() > 0) {
			while(collapsibleNode.getChildAt(collapsibleNode.childrenCount() - 1) instanceof CollapsibleNode) {
				collapsibleNode = (CollapsibleNode) collapsibleNode.getChildAt(collapsibleNode.childrenCount() - 1);
				if(collapsibleNode.childrenCount() == 0) {
					break;
				}
			}			
		}
		
		LastGrammarSlot slot = (LastGrammarSlot) collapsibleNode.getFirstPackedNodeGrammarSlot();
		return slot.getObject();
	}

	private void buildAmbiguityNode(NonterminalSymbolNode nonterminalSymbolNode) {
		
		int nPackedNodes = 0;
		
		for(SPPFNode child : nonterminalSymbolNode.getChildren()) {
			PackedNode packedNode = (PackedNode) child;
			
			Result<U> result;
			
			if(packedNode.getLastChild() instanceof CollapsibleNode) {
				CollapsibleNode lastChild = (CollapsibleNode) packedNode.getLastChild();
				Object object = getObject(lastChild);
				listener.startNode((T) object);
				
				removeCollapsibleNode(packedNode);
				
				packedNode.accept(this);
				result = listener.endNode((T) object, getChildrenValues(packedNode), 
						input.getPositionInfo(packedNode.getLeftExtent(), packedNode.getRightExtent()));
				packedNode.setObject(result);				
			} else {
				LastGrammarSlot slot = (LastGrammarSlot) packedNode.getGrammarSlot();
				listener.startNode((T) slot.getObject());
				packedNode.accept(this);
				result = listener.endNode((T) slot.getObject(), getChildrenValues(packedNode), 
						input.getPositionInfo(packedNode.getLeftExtent(), packedNode.getRightExtent()));
				packedNode.setObject(result);				
			}
			
			if(result != Result.filter()) {
				nPackedNodes++;
			}
		}
		
		if(nPackedNodes > 1) {
			Result<U> result = listener.buildAmbiguityNode(getChildrenValues(nonterminalSymbolNode), 
					input.getPositionInfo(nonterminalSymbolNode.getLeftExtent(), nonterminalSymbolNode.getRightExtent()));
			nonterminalSymbolNode.setObject(result);
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		// Intermediate nodes should be removed when visiting their parents.
		throw new RuntimeException("Should not be here!");
	}

	@Override
	public void visit(PackedNode packedNode) {
		removeIntermediateNode(packedNode);
		removeListSymbolNode(packedNode);
		
		if(!packedNode.isVisited()) {
			packedNode.setVisited(true);
			visitChildren(packedNode, this);
		}
	}

	@Override
	public void visit(ListSymbolNode listNode) {
		removeListSymbolNode(listNode);
		if(!listNode.isVisited()) {
			listNode.setVisited(true);
			if(listNode.isAmbiguous()) {
				buildAmbiguityNode(listNode);
			} else {
				LastGrammarSlot slot = (LastGrammarSlot) listNode.getFirstPackedNodeGrammarSlot();
				listener.startNode((T) slot.getObject());
				visitChildren(listNode, this);
				Result<U> result = listener.endNode((T) slot.getObject(), getChildrenValues(listNode), 
						input.getPositionInfo(listNode.getLeftExtent(), listNode.getRightExtent()));
				listNode.setObject(result);
			}
		}
	}
	
	private Iterable<U> getChildrenValues(final SPPFNode node) {

		final Iterator<SPPFNode> iterator = node.getChildren().iterator();

		return new Iterable<U>() {

			@Override
			public Iterator<U> iterator() {
				return new Iterator<U>() {

					private SPPFNode next;

					@Override
					public boolean hasNext() {
						while (iterator.hasNext()) {
							next = iterator.next();
							
							if(next.getObject() == Result.filter()) {
								node.setObject(Result.filter());
								// Go to the next child
								if(!iterator.hasNext()) {
									return false;
								}
								next = iterator.next();
							} 
							
							else if(next.getObject() == Result.skip()) {
								// Go to the next child
								if(!iterator.hasNext()) {
									return false;
								}
								next = iterator.next();
							}
							
							else if(next.getObject() == null) {
								if(!iterator.hasNext()) {
									return false;
								}
								next = iterator.next();
							}
							
							else {
								return true;								
							}
						}
						return false;
					}

					@Override
					public U next() {
						return ((Result<U>) next.getObject()).getObject();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
				
			}
		};
	}

	
}
