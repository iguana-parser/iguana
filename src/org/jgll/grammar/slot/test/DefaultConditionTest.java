package org.jgll.grammar.slot.test;

import java.io.Serializable;
import java.util.List;

import org.jgll.grammar.condition.SlotAction;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.logging.LoggerWrapper;

public class DefaultConditionTest implements ConditionTest, Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final LoggerWrapper log = LoggerWrapper.getLogger(DefaultConditionTest.class);

	private final List<? extends SlotAction<Boolean>> conditions;
	
	public DefaultConditionTest(List<? extends SlotAction<Boolean>> conditions) {
		this.conditions = conditions;
	}

	public boolean execute(GLLParser parser, Lexer lexer, GSSNode gssNode, int inputIndex) {
		for(SlotAction<Boolean> condition : conditions) {
			if(condition.execute(parser, lexer, gssNode, inputIndex)) {
				log.trace("Condition %s executed.", condition);
				return true;
			}
		}
		return false;
	}
	
	public Iterable<? extends SlotAction<Boolean>> getConditions() {
		return conditions;
	}

	@Override
	public String getConstructorCode() {
		StringBuilder sb = new StringBuilder();
		for (SlotAction<Boolean> slotAction : conditions) {
			sb.append(slotAction.getConstructorCode() + ", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		
		return "new DefaultConditionTest(list(" + sb.toString() + "))";
	}
	
}
