package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.symbol.Symbol;

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
		
	public abstract void codeIfTestSetCheck(Writer writer) throws IOException;
	
	public boolean isFirst() {
		return previous == null;
	}
	
	public boolean isLast() {
		return next.next == null;
	}
	
	public void codeElseTestSetCheck(Writer writer) throws IOException {
		writer.append("} else { newParseError(grammar.getGrammarSlot(" + this.id +  "), ci); label = L0; return; } \n");
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
	
}
