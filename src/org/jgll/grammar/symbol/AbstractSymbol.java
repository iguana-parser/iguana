package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;

import static org.jgll.util.generator.GeneratorUtil.*;

public abstract class AbstractSymbol implements Symbol {

	private static final long serialVersionUID = 1L;
	
	protected final Set<Condition> conditions;
	
	protected final String name;
	
	protected final Object object;
	
	protected final String label;
	
	public AbstractSymbol(String name, Set<Condition> conditions, String label, Object object) {
		this.name = name;
		this.label = label;
		this.object = object;
		this.conditions = Collections.unmodifiableSet(conditions);
	}
	
	@Override
	public Set<Condition> getConditions() {
		return conditions;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Object getObject() {
		return object;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public String toString() {
		return conditions.isEmpty() ? name :  "(" + name + listToString(conditions) + ")";
	}
	
	protected Set<Condition> getNotFollowConditions() {
		Set<Condition> set = new HashSet<>();
//		for (Condition condition : conditions) {
//			if (condition.getType() == ConditionType.NOT_FOLLOW) {
//				set.add(condition);
//			}
//		}
		return set;
	}
	
}
