package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class BodyGrammarSlot extends GrammarSlot implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected BodyGrammarSlot previous;
	
	protected BodyGrammarSlot next;
	
	/**
	 * The position from the beginning of the alternate.
	 * Positions start from zero.
	 * TODO: see if can be removed
	 */
	protected final int position;
	
	protected HeadGrammarSlot head;
	
	public BodyGrammarSlot(String label, int position, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(label);
		this.position = position;
		this.head = head;
		if(previous != null) {
			previous.next = this;
		}
		this.previous = previous;
	}
	
	/**
	 * Checks whether the provide input belongs to the first set, and follow set
	 * in case the first set contains epsilon.  
	 */
	public abstract boolean checkAgainstTestSet(int i);
	
	public abstract void codeIfTestSetCheck(Writer writer) throws IOException;
	
	public abstract Iterable<Terminal> getTestSet();
	
	public void codeElseTestSetCheck(Writer writer) throws IOException {
		writer.append("} else { newParseError(grammar.getGrammarSlot(" + this.id +  "), ci); label = L0; return; } \n");
	}
	
	public BodyGrammarSlot next() {
		return next;
	}
	
	public BodyGrammarSlot previous() {
		return previous;
	}
	
	public int getPosition() {
		return position;
	}
	
	public HeadGrammarSlot getHead() {
		return head;
	}
	
	/**
	 * Returns the grammar symbol after this slot 
	 */
	public abstract Symbol getSymbol();
	
	public abstract boolean isTerminalSlot();
	
	public abstract boolean isNonterminalSlot();
	
	public abstract boolean isLastSlot();
	
	public abstract boolean isNullable();
	
}
