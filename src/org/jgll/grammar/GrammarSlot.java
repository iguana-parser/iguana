package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Set;

/**
 * A GrammarSlot is the position immediately before or after
 * any symbol in an alternate. They are denoted by LR(0) items. 
 * In X ::= alpha . beta, the grammar symbol denoted by . is after
 * alpha and before beta.  
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class GrammarSlot implements Serializable {
	
	protected final int id;
	private final String name;
	protected final GrammarSlot previous;
	protected GrammarSlot next;
	
	/**
	 * The position from the beginning of the alternate.
	 * Positions start from zero.
	 */
	protected final int position;
	
	public GrammarSlot(int id, String label, int position, GrammarSlot previous) {
		this.id = id;
		this.name = label;
		this.position = position;
		this.previous = previous;
		if(previous != null) {
			previous.next = this;
		}
	}
	
	public abstract Set<Integer> getTestSet();
	
	public abstract void code(Writer writer) throws IOException;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public GrammarSlot next() {
		return next;
	}
	
	public GrammarSlot previous() {
		return previous;
	}
	
	public int getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
