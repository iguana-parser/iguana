package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

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
	
	protected BitSet predictionSet;
	
	public BodyGrammarSlot(int position, BodyGrammarSlot previous, HeadGrammarSlot head) {
		
		if(position < 0) {
			throw new IllegalArgumentException("Position cannot be negative.");
		}
		
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
	
	public void setPredictionSet(BitSet predictionSet) {
		this.predictionSet = predictionSet;
	}
	
	public BitSet getPredictionSet() {
		return predictionSet;
	}
	
	protected boolean executePreConditions(GLLParser parser, GLLLexer lexer) {
		for(SlotAction<Boolean> preCondition : preConditions) {
			if(preCondition.execute(parser, lexer)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Because some grammar slots e.g., keywords are directly created without 
	 * a pop action, at this point the popActions for the next slots
     * should be checked.
	 * Applicable for the case: Expr ::= "-" !>> [0-9] Expr
	 *								   | NegativeNumber
	 */
	protected boolean checkPopActions(GLLParser parser, GLLLexer lexer) {
		for(SlotAction<Boolean> slotAction : next.popActions) {
			if(slotAction.execute(parser, lexer)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean test(int inputIndex, GLLLexer lexer) {
		return predictionSet.intersects(lexer.tokenIDsAt(inputIndex));
	}
	
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
