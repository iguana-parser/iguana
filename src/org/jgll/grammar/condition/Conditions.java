package org.jgll.grammar.condition;

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
}
