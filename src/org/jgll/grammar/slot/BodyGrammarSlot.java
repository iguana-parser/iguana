package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.grammar.symbol.Rule;
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
	
	protected final BodyGrammarSlot previous;
	
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
	
	protected final String label;

	protected final Rule rule;

	protected final int slotId;
	
	public BodyGrammarSlot(Rule rule, int position, int slotId, String label, BodyGrammarSlot previous, HeadGrammarSlot head) {
		
		this.slotId = slotId;
		if(position < 0) {
			throw new IllegalArgumentException("Position cannot be negative.");
		}
		
		if(label == null) {
			throw new IllegalArgumentException("Label cannot be null.");
		}
		
		this.rule = rule;
		this.position = position;
		this.label = label;
		this.head = head;
		if(previous != null) {
			previous.next = this;
		}
		this.previous = previous;
		this.preConditions = new ArrayList<>();
		this.popActions = new ArrayList<>();
	}
	
	public abstract Symbol getSymbol();
	
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
	
	protected boolean executePreConditions(GLLParser parser, GLLLexer lexer) {
		for(SlotAction<Boolean> preCondition : preConditions) {
			if(preCondition.execute(parser, lexer, parser.getCurrentInputIndex())) {
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
	protected boolean executePopActions(GLLParser parser, GLLLexer lexer) {
		for(SlotAction<Boolean> slotAction : next.popActions) {
			if(slotAction.execute(parser, lexer, parser.getCurrentInputIndex())) {
				return true;
			}
		}
		return false;
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

	public Rule getRule() {
		return rule;
	}
	
	public int getPosition() {
		return position;
	}
	
	public HeadGrammarSlot getHead() {
		return head;
	}
	
	public String getLabel() {
		return label;
	}
	
	public abstract boolean isNullable();
	
	public abstract boolean isNameEqual(BodyGrammarSlot slot);
	
	@Override
	public String toString() {
		return label;
	}
	
}
