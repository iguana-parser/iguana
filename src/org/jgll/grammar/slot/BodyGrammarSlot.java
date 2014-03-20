package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.jgll.grammar.slot.test.ConditionsTest;
import org.jgll.grammar.symbol.Symbol;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class BodyGrammarSlot implements GrammarSlot, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected final int id;

	protected final BodyGrammarSlot previous;
	
	protected BodyGrammarSlot next;
	
	protected final String label;

	public BodyGrammarSlot(int id, String label, BodyGrammarSlot previous) {
		
		this.id = id;
		
		if(label == null) {
			throw new IllegalArgumentException("Label cannot be null.");
		}
		
		this.label = label;

		if(previous != null) {
			previous.next = this;
		}
		
		this.previous = previous;
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
	
	public abstract ConditionsTest getPreConditions();
	
	public abstract ConditionsTest getPostConditions();
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
}
