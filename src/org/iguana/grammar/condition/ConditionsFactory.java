package org.jgll.grammar.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.datadependent.env.IEvaluatorContext;
import org.jgll.datadependent.env.persistent.PersistentEvaluatorContext;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

public class ConditionsFactory {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(ConditionsFactory.class);
	
	public static Conditions getConditions(Set<Condition> conditions) {
		if (conditions.size() == 0)
			return (input, u, i) -> false;
		
		List<Condition> list = new ArrayList<>(conditions);
		
		boolean requiresEnvironment = false;
		for (Condition c : list) {
			if (c.isDataDependent()) {
				requiresEnvironment = true;
				break;
			}
		}
		
		if (requiresEnvironment) {
			return new Conditions() {

				@Override
				public boolean execute(Input input, GSSNode u, int i) {
					return execute(input, u, i, new PersistentEvaluatorContext(input));
				}
				
				@Override
				public boolean execute(Input input, GSSNode u, int i, IEvaluatorContext ctx) {
					for (Condition c : list) {
			            if (c.getSlotAction().execute(input, u, i, ctx)) {
			                log.trace("Condition %s executed with %s", c, ctx.getEnvironment());
			                return true;
			            }
			        }
			        return false;
				}
				
			};
		}
		
		return (input, u, i) -> {
	        for (Condition c : list) {
	            if (c.getSlotAction().execute(input, u, i)) {
	                log.trace("Condition %s executed", c);
	                return true;
	            }
	        }
	        return false;
		};
	}

}
