package org.jgll.grammar.symbol;

import java.util.HashMap;
import java.util.Map;

import org.jgll.util.generator.GeneratorUtil;


public class AssociativityGroup {
	
	private final Associativity associativity;
	
	private final PrecedenceLevel precedenceLevel;
	
	private final Map<Integer, Associativity> map;
	
	private int precedence = -1;
	
	private int lhs = -1;
	
	private int rhs = -1;

	public AssociativityGroup(Associativity associativity, PrecedenceLevel precedenceLevel) {
		this.associativity = associativity;
		this.precedenceLevel = precedenceLevel;
		this.map = new HashMap<>();
		this.lhs = precedenceLevel.getLhs();
	}
	
	public AssociativityGroup(Associativity associativity, PrecedenceLevel precedenceLevel, int lhs, int rhs, int precedence, int undefined) {
		this(associativity, precedenceLevel);
		this.precedence = precedence;
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public Associativity getAssociativity() {
		return associativity;
	}
	
	public int getLhs() {
		return lhs;
	}
	
	public int getRhs() {
		return rhs;
	}
	
	public int getPrecedence() {
		return precedence;
	}
	
	public Map<Integer, Associativity> getAssocMap() {
		return map;
	}
	
	public AssociativityGroup add(int precedence, Associativity associativity) {
		
		if (precedence == this.precedence) return this;
		
		map.put(precedence, associativity);
		return this;
	}
		
	public int getPrecedence(Rule rule) {
		if (!rule.isLeftOrRightRecursive())
			return -1;
		
		if (rule.getAssociativity() == associativity) {
			if (precedence != -1)
				return precedence;
			else {
				precedence = precedenceLevel.getPrecedence(rule);
				return precedence;
			}
		} 
		
		if (rule.getAssociativity() == Associativity.UNDEFINED) {
			if (precedence != -1)
				return precedence;
			else {
				precedence = precedenceLevel.getPrecedenceFromAssociativityGroup(rule);
				return precedence;
			}
		}
		
		int precedence = precedenceLevel.getPrecedence(rule);
		map.put(precedence, rule.getAssociativity());
		return precedence;
	}
	
	public void done() {
		rhs = precedenceLevel.getCurrent();
	}
	
	public String getConstructorCode() {
		String elements = "";
		
		for (Map.Entry<Integer, Associativity> entry : map.entrySet())
			elements += ".add(" + entry.getKey() + "," + entry.getValue().getConstructorCode() + ")";
		
		return "new " + getClass().getSimpleName() + "(" + associativity.getConstructorCode() + "," 
														 + precedenceLevel.getConstructorCode() + ","
														 + lhs + ","
														 + rhs + ","
														 + precedence + ")" + elements;
	}
	
	@Override
	public String toString() {
		return associativity.name() + "(" 
					+ lhs + ","
					+ rhs + ","
					+ (precedence != -1? precedence + (map.keySet().isEmpty()? "" : ",") : "") 
					+ GeneratorUtil.listToString(map.keySet(), ",") + ")";
	}

}
