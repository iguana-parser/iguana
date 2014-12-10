package org.jgll.grammar.condition;

import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

@FunctionalInterface
public interface SlotAction {
	public boolean execute(Input input, GSSNode gssNode, int inputIndex);
}
