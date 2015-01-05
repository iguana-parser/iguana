package org.jgll.grammar.symbol;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;

public class Expression implements Symbol {

	private static final long serialVersionUID = 1L;
	
	private final org.jgll.datadependent.exp.Expression[] expressions;
	
	Expression(org.jgll.datadependent.exp.Expression[] expressions) {
		this.expressions = expressions;
	}
	
	public static Expression block(org.jgll.datadependent.exp.Expression... expressions) {
		return new Expression(expressions);
	}
	
	public  org.jgll.datadependent.exp.Expression[] getExpressions() {
		return expressions;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Set<Condition> getPreConditions() {
		return new HashSet<>();
	}

	@Override
	public Set<Condition> getPostConditions() {
		return new HashSet<>();
	}

	@Override
	public Object getObject() {
		return null;
	}

	@Override
	public String getLabel() {
		return null;
	}

}
