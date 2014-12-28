package org.jgll.grammar.slot;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Position;


public class BodyGrammarSlot extends AbstractGrammarSlot {

	private final Position position;

	public BodyGrammarSlot(Position position) {
		this.position = position;
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return new StringBuilder()
    	  .append("new BodyGrammarSlot(")
    	  .append(")").toString();
	}
	
	@Override
	public String toString() {
		return position.toString();
	}
}
