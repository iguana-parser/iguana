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
	
	protected final Rule rule;
	
	protected final BodyGrammarSlot previous;
	
	protected BodyGrammarSlot next;
	
	private final String label;
	
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
		
		String tmp = rule.getHead() + " ::= ";
		int i = 0;
		for(Symbol s : rule.getBody()) {
			if(i++ == position) {
				tmp += ". ";
			}
			tmp += s + " ";
		}
		if(position == rule.getBody().size()) {
			tmp += ".";
		}
		label = tmp;
	}
	
	/**
	 * Checks whether the provide input belogs to the first set, and follow set
	 * in case the first set contains epsilon.  
	 */
	public abstract boolean checkAgainstTestSet(int i);
	
	public abstract void codeIfTestSetCheck(Writer writer) throws IOException;
	
	public void codeElseTestSetCheck(Writer writer) throws IOException {
		writer.append("} else { label = L0; } \n");
	}
	
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
	
	@Override
	public String toString() {
		return label;
	}
}
