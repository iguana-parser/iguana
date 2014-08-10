package org.jgll.traversal;

import static org.jgll.traversal.SPPFVisitorUtil.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.Sequence;
import org.jgll.sppf.CollapsibleNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;

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

	private GrammarGraph grammar;
	
	public ModelBuilderVisitor(Input input, NodeListener<T, U> listener, GrammarGraph grammar) {
		this.input = input;
		this.listener = listener;
		this.grammar = grammar;
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		removeIntermediateNode(node);
		
		if(!node.isVisited()) {
		
			node.setVisited(true);
			
			if(node.isAmbiguous()) {
				buildAmbiguityNode(node);
			}
			else {
				
				T object = (T) grammar.getObject(node.getId(), node.getFirstPackedNodeGrammarSlot());
				
				Result<U> result;
				
				if(node.getChildAt(node.childrenCount() - 1) instanceof CollapsibleNode) {
					CollapsibleNode lastChild = (CollapsibleNode) node.getChildAt(node.childrenCount() - 1);
					Object lastChildObject = getObject(lastChild);
					listener.startNode((T) lastChildObject);
					visitChildren(node, this);
					removeCollapsibleNode(node);
					result = listener.endNode((T) lastChildObject, getChildrenValues(node), 
								input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
				} else {
					listener.startNode(object);
					visitChildren(node, this);
					result = listener.endNode(object, getChildrenValues(node), 
								input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
				}

				node.setObject(result);
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
		
		
		return grammar.getObject(node.getId(), node.getFirstPackedNodeGrammarSlot());
	}

	private void buildAmbiguityNode(NonterminalSymbolNode nonterminalSymbolNode) {
		
		int nPackedNodes = 0;
		
		List<SPPFNode> list = new ArrayList<>();			
		for(SPPFNode node : nonterminalSymbolNode.getChildren()) {
			list.add(node);
		}
		for(SPPFNode child : list) {
			PackedNode node = (PackedNode) child;
			
			Result<U> result;
			
			if(node.childrenCount() > 0 && node.getChildAt(node.childrenCount() - 1) instanceof CollapsibleNode) {
				CollapsibleNode lastChild = (CollapsibleNode) node.getChildAt(node.childrenCount() - 1);
				Object object = getObject(lastChild);
				listener.startNode((T) object);
				
				removeCollapsibleNode(node);
				
				node.accept(this);
				result = listener.endNode((T) object, getChildrenValues(node), input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
				node.setObject(result);				
			} else {
				T object = (T) grammar.getObject(nonterminalSymbolNode.getId(), nonterminalSymbolNode.getFirstPackedNodeGrammarSlot());
				listener.startNode(object);
				node.accept(this);
				result = listener.endNode(object, getChildrenValues(node), 
						input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
				node.setObject(result);				
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
			}	
			else {
				T object = (T) grammar.getObject(listNode.getId(), listNode.getFirstPackedNodeGrammarSlot());
				listener.startNode(object);
				visitChildren(listNode, this);
				Result<U> result = listener.endNode(object, getChildrenValues(listNode), 
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

	@Override
	public void visit(TokenSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			
			RegularExpression regex = grammar.getRegularExpressionById(node.getTokenID());
			
			if (regex instanceof Sequence) {
				Sequence<CharacterClass> sequence = (Sequence<CharacterClass>) regex;
				Object object = regex.getObject();
				listener.startNode((T) object);
				
				List<U> childrenVal = new ArrayList<>();
				for (int i = 0; i < sequence.getRegularExpressions().size(); i++) {
					int c = sequence.get(i).get(0).getStart();
					Result<U> t = listener.terminal(c, input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
					childrenVal.add(t.getObject());
				}
				
				Result<U> result = listener.endNode((T) object, childrenVal, 
						input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
				node.setObject(result);
			}
			
			// For now we only support parse tree generation for character class
			else if (regex instanceof CharacterClass) {
				int c = input.charAt(node.getLeftExtent());
				Result<U> result = listener.terminal(c, input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
				node.setObject(result);
			}
			
			else {
				throw new RuntimeException("Should not come here!");
			}
		}	
	}
	
}
