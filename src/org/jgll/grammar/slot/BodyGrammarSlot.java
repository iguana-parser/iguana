package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.SlotAction;
import org.jgll.grammar.Symbol;
import org.jgll.util.Input;

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
	
	protected List<SlotAction<Boolean>> preConditions;
	
	public BodyGrammarSlot(String label, int position, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(label);
		this.position = position;
		this.head = head;
		if(previous != null) {
			previous.next = this;
		}
		this.previous = previous;
		this.preConditions = new ArrayList<>();
	}
	
		
	public void addPreCondition(SlotAction<Boolean> preCondition) {
		preConditions.add(preCondition);
	}
	
	public List<SlotAction<Boolean>> getPreConditions() {
		return preConditions;
	}
	
	public void setPreConditions(List<SlotAction<Boolean>> preConditions) {
		this.preConditions = preConditions;
	}

	/**
	 * Checks whether the character at the provided input index belongs to the first set  
	 */
	public abstract boolean testFirstSet(int index, Input input);
	
	/**
	 * Checks whether the character at the provided input index belongs to the follow set.
	 * This method should be called if the nonterminal is nullable.
	 */
	public abstract boolean testFollowSet(int index, Input input);
	
	public abstract void codeIfTestSetCheck(Writer writer) throws IOException;
	
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
	
	public abstract boolean isNullable();
	
}
