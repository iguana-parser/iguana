package org.jgll.grammar.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.util.logging.LoggerWrapper;

public class ConditionsFactory {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(ConditionsFactory.class);
	
	public static Conditions getConditions(Set<Condition> conditions) {
		if (conditions.size() == 0)
			return (input, u, i) -> false;
		
		List<Condition> list = new ArrayList<>(conditions);
			
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
