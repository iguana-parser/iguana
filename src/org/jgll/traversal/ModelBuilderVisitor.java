package org.jgll.traversal;

import static org.jgll.traversal.SPPFVisitorUtil.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.EndGrammarSlot;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.Sequence;
import org.jgll.sppf.CollapsibleNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalNode;
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
	
	private Set<SPPFNode> visited = new HashSet<>();
	
	private Map<SPPFNode, Object> objects = new HashMap<>();
	
	public ModelBuilderVisitor(Input input, NodeListener<T, U> listener, GrammarGraph grammar) {
		this.input = input;
		this.listener = listener;
	}

	@Override
	public void visit(NonterminalNode node) {
		removeIntermediateNode(node);
		
		if (!visited.contains(node)) {
			visited.add(node);
						
			if(node.isAmbiguous()) {
				buildAmbiguityNode(node);
			}
			else {
				
				T object = (T) getObject(node);
				
				Result<U> result;
				
//				if(node.getChildAt(node.childrenCount() - 1) instanceof CollapsibleNode) {
//					CollapsibleNode lastChild = (CollapsibleNode) node.getChildAt(node.childrenCount() - 1);
//					Object lastChildObject = getObject(lastChild);
//					listener.startNode((T) lastChildObject);
//					visitChildren(node, this);
//					removeCollapsibleNode(node);
//					result = listener.endNode((T) lastChildObject, getChildrenValues(node), 
//								input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
//				} else {
//					listener.startNode(object);
//					visitChildren(node, this);
//					result = listener.endNode(object, getChildrenValues(node), 
//								input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
//				}

//				objects.put(node, result);
			}
		}
	}
	
	private Object getObject(NonterminalNode node) {
		assert node.getChildAt(0) instanceof PackedNode;
		PackedNode packedNode = (PackedNode) node.getChildAt(0);
		
		assert packedNode.getGrammarSlot() instanceof EndGrammarSlot;
		EndGrammarSlot grammarSlot = (EndGrammarSlot) packedNode.getGrammarSlot();
		return grammarSlot.getObject();
	}
	
	// TODO: does not work for collapsible nodes which are ambiguous.
	// They should be lifted, similar to intermediate nodes.
	private Object getObject(CollapsibleNode node) {
		
//		CollapsibleNode collapsibleNode = node;
//		if(collapsibleNode.childrenCount() > 0) {
//			while(collapsibleNode.getChildAt(collapsibleNode.childrenCount() - 1) instanceof CollapsibleNode) {
//				collapsibleNode = (CollapsibleNode) collapsibleNode.getChildAt(collapsibleNode.childrenCount() - 1);
//				if(collapsibleNode.childrenCount() == 0) {
//					break;
//				}
//			}			
//		}
		
		return getObject(node);
	}

	private void buildAmbiguityNode(NonterminalNode nonterminalSymbolNode) {
		
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
				result = listener.endNode((T) object, getChildrenValues(node), input.getPositionInfo(node.getParent().getLeftExtent(), node.getParent().getRightExtent()));
				objects.put(node, result);
			} else {
				T object = (T) getObject(nonterminalSymbolNode);
				listener.startNode(object);
				node.accept(this);
				result = listener.endNode(object, getChildrenValues(node), input.getPositionInfo(node.getParent().getLeftExtent(), node.getParent().getRightExtent()));
				objects.put(node, result);
			}
			
			if(result != Result.filter()) {
				nPackedNodes++;
			}
		}
		
		if(nPackedNodes > 1) {
			Result<U> result = listener.buildAmbiguityNode(getChildrenValues(nonterminalSymbolNode), 
					input.getPositionInfo(nonterminalSymbolNode.getLeftExtent(), nonterminalSymbolNode.getRightExtent()));
			objects.put(nonterminalSymbolNode, result);
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		// Intermediate nodes should be removed when visiting their parents.
		throw new RuntimeException("Should not be here!");
	}

	@Override
	public void visit(PackedNode node) {
		removeIntermediateNode(node);
		removeListSymbolNode(node);

		if (!visited.contains(node)) {
			visited.add(node);
			visitChildren(node, this);
		}
	}

	@Override
	public void visit(ListSymbolNode node) {
		removeListSymbolNode(node);
		
		if (!visited.contains(node)) {
			visited.add(node);

			if(node.isAmbiguous()) {
				buildAmbiguityNode(node);
			}	
			else {
				T object = (T) getObject(node);
				listener.startNode(object);
				visitChildren(node, this);
				Result<U> result = listener.endNode(object, getChildrenValues(node), 
						input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
				objects.put(node, result);
			}
		}
	}
	
	private Iterable<U> getChildrenValues(final SPPFNode node) {

		final Iterator<? extends SPPFNode> iterator = node.getChildren().iterator();

		return new Iterable<U>() {

			@Override
			public Iterator<U> iterator() {
				return new Iterator<U>() {

					private SPPFNode next;

					@Override
					public boolean hasNext() {
						while (iterator.hasNext()) {
							next = iterator.next();
							
							if(objects.get(next) == Result.filter()) {
								objects.put(node, Result.filter());
								// Go to the next child
								if(!iterator.hasNext()) {
									return false;
								}
								next = iterator.next();
							} 
							
							else if(objects.get(next) == Result.skip()) {
								// Go to the next child
								if(!iterator.hasNext()) {
									return false;
								}
								next = iterator.next();
							}
							
							else if(objects.get(next) == null) {
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
						return ((Result<U>) objects.get(next)).getObject();
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
	public void visit(TerminalNode node) {
		if (!visited.contains(node)) {
			visited.add(node);
			
			RegularExpression regex = node.getGrammarSlot().getRegularExpression();
			
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
				objects.put(node, result);
			}
			
			// For now we only support parse tree generation for character class
			else if (regex instanceof CharacterClass) {
				int c = input.charAt(node.getLeftExtent());
				Result<U> result = listener.terminal(c, input.getPositionInfo(node.getLeftExtent(), node.getRightExtent()));
				objects.put(node, result);
			}
			
			else {
				throw new RuntimeException("Should not come here!");
			}
		}	
	}
	
}
