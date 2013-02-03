package org.jgll.grammar;

import java.io.Serializable;
import java.util.Set;

public abstract class BodyGrammarSlot extends GrammarSlot implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected final Rule rule;
	
	protected final BodyGrammarSlot previous;
	
	protected BodyGrammarSlot next;
	
	/**
	 * The position from the beginning of the alternate.
	 * Positions start from zero.
	 */
	protected final int position;
		
	public BodyGrammarSlot(Rule rule, int id, int position, BodyGrammarSlot previous) {
		super(id);
		this.position = position;
		this.rule = rule;
		if(previous != null) {
			previous.next = this;
		}
		this.previous = previous;		
	}
	
	public abstract Set<Terminal> getTestSet();
	
	public GrammarSlot next() {
		return next;
	}
	
	public GrammarSlot previous() {
		return previous;
	}
	
	public Nonterminal getHead() {
		return rule.getHead();
	}
	
	public int getPosition() {
		return position;
	}
	
}
