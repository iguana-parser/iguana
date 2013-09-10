package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.Symbol;
import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.parser.GLLParserInternals;
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
	
	protected List<SlotAction<Boolean>> popActions;
	
	private String label;
	
	public BodyGrammarSlot(int position, BodyGrammarSlot previous, HeadGrammarSlot head) {
		this.position = position;
		this.head = head;
		if(previous != null) {
			previous.next = this;
		}
		this.previous = previous;
		this.preConditions = new ArrayList<>();
		this.popActions = new ArrayList<>();
	}
	
	public void addPopAction(SlotAction<Boolean> popAction) {
		popActions.add(popAction);
	}
	
	public Iterable<SlotAction<Boolean>> getPopActions() {
		return popActions;
	}
		
	public void addPreCondition(SlotAction<Boolean> preCondition) {
		preConditions.add(preCondition);
	}
	
	public List<SlotAction<Boolean>> getPreConditions() {
		return preConditions;
	}
	
	protected boolean executePreConditions(GLLParserInternals parser, Input input) {
		for(SlotAction<Boolean> preCondition : preConditions) {
			if(preCondition.execute(parser, input)) {
				return true;
			}
		}

		return false;
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
	
	
	public void setPrevious(BodyGrammarSlot previous) {
		this.previous = previous;
	}
	
	public void setNext(BodyGrammarSlot next) {
		this.next = next;
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
	
	public abstract boolean isNameEqual(BodyGrammarSlot slot);
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
}
