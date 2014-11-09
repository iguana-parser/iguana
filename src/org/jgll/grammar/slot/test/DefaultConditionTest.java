package org.jgll.grammar.slot.test;

import java.io.Serializable;
import java.util.List;

import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.logging.LoggerWrapper;

public class DefaultConditionTest implements ConditionTest, Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final LoggerWrapper log = LoggerWrapper.getLogger(DefaultConditionTest.class);

	private final List<SlotAction<Boolean>> conditions;
	
	public DefaultConditionTest(List<SlotAction<Boolean>> conditions) {
		this.conditions = conditions;
	}

	public boolean execute(GLLParser parser, GLLLexer lexer, GSSNode gssNode, int inputIndex) {
		for(SlotAction<Boolean> condition : conditions) {
			if(condition.execute(parser, lexer, gssNode, inputIndex)) {
				log.trace("Condition %s executed.", condition);
				return true;
			}
		}
		return false;
	}
	
	public Iterable<SlotAction<Boolean>> getConditions() {
		return conditions;
	}

	@Override
	public String getConstructorCode() {
		StringBuilder sb = new StringBuilder();
		sb.append("List<ConditionTest> list = new ArrayList();");
		for (SlotAction<Boolean> slotAction : conditions) {
			sb.append("list.add(" + slotAction.toCode() + ");");
		}
		sb.append("ConditionTest test  = new ConditionTest(list);");
		return sb.toString();
	}
	
}
