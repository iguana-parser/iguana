package org.jgll.grammar.slot.test;

import java.util.List;

import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public class DefaultConditionTest implements ConditionTest {

	private final List<SlotAction<Boolean>> conditions;
	
	public DefaultConditionTest(List<SlotAction<Boolean>> conditions) {
		this.conditions = conditions;
	}

	public boolean execute(GLLParser parser, GLLLexer lexer, int inputIndex) {
		for(SlotAction<Boolean> condition : conditions) {
			if(condition.execute(parser, lexer, inputIndex)) {
				return true;
			}
		}
		return false;
	}
	
	public Iterable<SlotAction<Boolean>> getConditions() {
		return conditions;
	}
	
}
