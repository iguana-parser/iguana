package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.util.CollectionsUtil;

public abstract class AbstractSymbol implements Symbol {

	private static final long serialVersionUID = 1L;
	
	protected Set<Condition> conditions;
	
	protected String name;
	
	public AbstractSymbol(String name) {
		this(name, Collections.<Condition>emptySet());
	}
	
	public AbstractSymbol(String name, Set<Condition> conditions) {
		this.name = name;
		this.conditions = new HashSet<>(conditions);
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
	public String toString() {
		return conditions.isEmpty() ? name : "(" + name + CollectionsUtil.listToString(conditions) + ")";
	}
	
	@Override
	public Symbol withCondition(Condition condition) {
		Set<Condition> conditions = new HashSet<>();
		conditions.add(condition);
		return withConditions(conditions);
	}
	
	protected Set<Condition> getNotFollowConditions() {
		Set<Condition> set = new HashSet<>();
		for (Condition condition : conditions) {
			if (condition.getType() == ConditionType.NOT_FOLLOW) {
				set.add(condition);
			}
		}
		return set;
	}
	
}
