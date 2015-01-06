package org.jgll.grammar.condition;

import org.jgll.datadependent.env.Environment;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

@FunctionalInterface
public interface SlotAction {
	public boolean execute(Input input, GSSNode gssNode, int inputIndex, Environment env);
}
