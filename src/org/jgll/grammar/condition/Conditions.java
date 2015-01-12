package org.jgll.grammar.condition;

import java.util.Set;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;
import org.jgll.util.generator.ConstructorCode;
import org.jgll.util.logging.LoggerWrapper;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Conditions implements ConstructorCode {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(Conditions.class);
	
	private Set<Condition> conditions;

	public Conditions(Set<Condition> conditions) {
		this.conditions = conditions;
	}
	
	public boolean execute(Input input, GSSNode u, int i) {
		for (Condition c : conditions) {
			if (c.getSlotAction().execute(input, u, i)) {
				log.trace("Condition " + c + " executed");
				return true;
			}
		}
		return false;
	}

	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		return "new Conditions(" + getConstructorCode(conditions, registry) + ")";
	}
	
}
