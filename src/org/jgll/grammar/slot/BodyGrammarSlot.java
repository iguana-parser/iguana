package org.jgll.grammar.slot;

import java.util.List;

import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class BodyGrammarSlot implements GrammarSlot {
	
	protected final int id;

	protected final BodyGrammarSlot previous;
	
	protected BodyGrammarSlot next;
	
	protected final String label;
	
	protected final ConditionTest preConditions;

	protected final ConditionTest postConditions;
	
	protected final ConditionTest popConditions;
	
	protected final NodeCreator nodeCreator;
	
	protected final NodeCreator nodeCreatorFromPop;
	
	private GSSNode[] gssNodes;

	public BodyGrammarSlot(int id, String label, BodyGrammarSlot previous, 
						   ConditionTest preConditions, ConditionTest postConditions, ConditionTest popConditions,
						   NodeCreator nodeCreator, NodeCreator nodeCreatorFromPop) {

		if(label == null) {
			throw new IllegalArgumentException("Label cannot be null.");
		}
		
		this.id = id;
		
		this.label = label;

		if(previous != null) {
			previous.next = this;
		}
		
		this.previous = previous;
		this.preConditions = preConditions;
		this.postConditions = postConditions;
		this.popConditions = popConditions;
		this.nodeCreator = nodeCreator;
		this.nodeCreatorFromPop = nodeCreatorFromPop;
	}
	
	public abstract Symbol getSymbol();
		
	public boolean isFirst() {
		return previous == null;
	}
	
	public boolean isLast() {
		return next.next == null;
	}
	
	public BodyGrammarSlot next() {
		return next;
	}
	
	public BodyGrammarSlot previous() {
		return previous;
	}

	public String getLabel() {
		return label;
	}
	
	public abstract boolean isNullable();
	
	public NodeCreator getNodeCreatorFromPop() {
		return nodeCreatorFromPop;
	}
	
	public NodeCreator getNodeCreator() {
		return nodeCreator;
	}
	
	public final ConditionTest getPreConditions() {
		return preConditions;
	}
	
	public final ConditionTest getPostConditions() {
		return postConditions;
	}
	
	public final ConditionTest getPopConditions() {
		return popConditions;
	}	
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	public static String getSlotName(Rule rule, int index) {
		
		Nonterminal head = rule.getHead();
		List<Symbol> body = rule.getBody();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(head.toString()).append(" ::= ");
		
		for(int i = 0; i < body.size(); i++) {
			Symbol s = body.get(i);
			
			if(i == index) {
				sb.append(". ");
			}
			
			if(s instanceof Nonterminal) {
				sb.append(s.toString());
			} else {
				sb.append(s);				
			}
			
			sb.append(" ");
		}

		if(index == body.size()) {
			sb.append(".");
		} else {
			sb.delete(sb.length() - 1, sb.length());			
		}
		
		return sb.toString();
	}
	
	@Override
	public GSSNode getGSSNode(int inputIndex) {
		GSSNode gssNode = new GSSNode(this, inputIndex);
		gssNodes[inputIndex] = gssNode;
		return gssNode;
	}

	@Override
	public GSSNode hasGSSNode(int inputIndex) {
		return gssNodes == null? null : gssNodes[inputIndex];
	}

	@Override
	public void init(Input input) {
		gssNodes = new GSSNode[input.length()];
	}
	
	@Override
	public void reset() {
		gssNodes = null;		
	}
	
	@Override
	public boolean isInitialized() {
		return gssNodes != null;
	}
	
}
