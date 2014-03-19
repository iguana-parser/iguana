package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.jgll.grammar.symbol.Symbol;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class BodyGrammarSlot implements GrammarSlot, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected final BodyGrammarSlot previous;
	
	protected final int id;
	
	protected BodyGrammarSlot next;
	
	protected HeadGrammarSlot head;
	
	protected final String label;

	public BodyGrammarSlot(int id, String label, BodyGrammarSlot previous, HeadGrammarSlot head) {
		
		this.id = id;
		
		if(label == null) {
			throw new IllegalArgumentException("Label cannot be null.");
		}
		
		this.label = label;
		this.head = head;
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
	
	public void codeElseTestSetCheck(Writer writer) throws IOException {
		writer.append("} else { newParseError(grammar.getGrammarSlot(" + this.id +  "), ci); label = L0; return; } \n");
	}
	
	public BodyGrammarSlot next() {
		return next;
	}
	
	public BodyGrammarSlot previous() {
		return previous;
	}

	public HeadGrammarSlot getHead() {
		return head;
	}
	
	public String getLabel() {
		return label;
	}
	
	public abstract boolean isNullable();
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
}
