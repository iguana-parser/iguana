package org.jgll.grammar.slot.test;

import java.util.List;

import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public class ConditionsTest {
	
	private List<SlotAction<Boolean>> conditions;

	public boolean execute(GLLParser parser, GLLLexer lexer) {
		for(SlotAction<Boolean> condition : conditions) {
			if(condition.execute(parser, lexer, parser.getCurrentInputIndex())) {
				return true;
			}
		}
		return false;
	}
	
	public void addCondition(SlotAction<Boolean> preCondition) {
		conditions.add(preCondition);
	}
	
	
	public Iterable<SlotAction<Boolean>> getConditions() {
		return conditions;
	}
	
}
