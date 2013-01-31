package org.jgll.grammar;

import java.io.Serializable;
import java.util.Set;

public abstract class BodyGrammarSlot extends GrammarSlot implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected final BodyGrammarSlot previous;
	protected BodyGrammarSlot next;
	
	/**
	 * The position from the beginning of the alternate.
	 * Positions start from zero.
	 */
	protected final int position;

	
	public BodyGrammarSlot(int id, String name, int position, BodyGrammarSlot previous) {
		super(id, name);
		this.position = position;
		this.previous = previous;
		if(previous != null) {
			previous.next = this;
		}
	}
	
	public abstract Set<Terminal> getTestSet();
	
	public GrammarSlot next() {
		return next;
	}
	
	public GrammarSlot previous() {
		return previous;
	}
	
	public int getPosition() {
		return position;
	}

}
