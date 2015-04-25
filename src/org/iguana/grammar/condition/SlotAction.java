package org.iguana.grammar.condition;

import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.Input;

@FunctionalInterface
public interface SlotAction {
	public boolean execute(Input input, GSSNode gssNode, int inputIndex);
	
	default boolean execute(Input input, GSSNode gssNode, int inputIndex, IEvaluatorContext ctx) {
		return execute(input, gssNode, inputIndex);
	}
}
