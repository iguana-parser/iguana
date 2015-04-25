package org.iguana.grammar.condition;

import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.Input;


/**
 * 
 * @author Ali Afroozeh
 *
 */
@FunctionalInterface
public interface Conditions {
	
	public boolean execute(Input input, GSSNode u, int i);
	
	default boolean execute(Input input, GSSNode u, int i, IEvaluatorContext ctx) {
		return execute(input, u, i);
	}
	
}
