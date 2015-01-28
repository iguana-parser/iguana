package org.jgll.grammar.condition;

import org.jgll.datadependent.env.Environment;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;


/**
 * 
 * @author Ali Afroozeh
 *
 */
@FunctionalInterface
public interface Conditions {
	
	public boolean execute(Input input, GSSNode u, int i);
	
	default boolean execute(Input input, GSSNode u, int i, Environment env) {
		return execute(input, u, i);
	}
	
}
