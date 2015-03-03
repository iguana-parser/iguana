package org.jgll.grammar.symbol;

import java.util.HashMap;
import java.util.Map;

import org.jgll.util.generator.GeneratorUtil;


public class AssociativityGroup {
	
	private final Associativity associativity;
	
	private final PrecedenceLevel precedenceGroup;
	
	private final Map<Integer, Associativity> map;
	
	private int precedence = -1;

	public AssociativityGroup(Associativity associativity, PrecedenceLevel precedenceGroup) {
		this.associativity = associativity;
		this.precedenceGroup = precedenceGroup;
		this.map = new HashMap<>();
	}
	
	public int getPrecedence() {
		return precedence;
	}
	
	public Map<Integer, Associativity> getAssocMap() {
		return map;
	}
		
	public int getPrecedence(Rule rule) {
		if (!rule.isLeftOrRightRecursive())
			return -1;
		
		if (rule.getAssociativity() == associativity) {
			if (precedence != -1)
				return precedence;
			else {
				precedence = precedenceGroup.getPrecedence(rule);
				return precedence;
			}
		}
		
		int precedence = precedenceGroup.getPrecedence(rule);
		map.put(precedence, rule.getAssociativity());
		return precedence;
	}
	
	public Associativity getAssociativity() {
		return associativity;
	}
	
	public String getConstructorCode() {
		return "new " + getClass().getSimpleName() + "(" + associativity.getConstructorCode() + "," + precedenceGroup.getConstructorCode() + ")";
	}
	
	@Override
	public String toString() {
		return associativity.name() + "(" + precedence + "," + GeneratorUtil.listToString(map.keySet(), ",") + ")";
	}

}
