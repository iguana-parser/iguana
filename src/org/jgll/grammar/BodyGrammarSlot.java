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
	
	protected final BodyGrammarSlot previous;
	
	protected BodyGrammarSlot next;
	
	/**
	 * The position from the beginning of the alternate.
	 * Positions start from zero.
	 */
	protected final int position;
		
	public BodyGrammarSlot(int id, int position, BodyGrammarSlot previous) {
		super(id);
		this.position = position;
		if(previous != null) {
			previous.next = this;
		}
		this.previous = previous;
		
//		// TODO: use getName() to generate the full slot presentation.
//		String tmp = rule.getHead() + " ::= ";
//		int i = 0;
//		for(Symbol s : rule.getBody()) {
//			if(i++ == position) {
//				tmp += ". ";
//			}
//			tmp += s + " ";
//		}
//		if(position == rule.getBody().size()) {
//			tmp += ".";
//		}
//		label = tmp;
	}
	
	/**
	 * Checks whether the provide input belogs to the first set, and follow set
	 * in case the first set contains epsilon.  
	 */
	public abstract boolean checkAgainstTestSet(int i);
	
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
	
	@Override
	public String toString() {
		//TODO: add this
		return "";
	}
	
	public abstract Iterable<Terminal> getTestSet();
}
