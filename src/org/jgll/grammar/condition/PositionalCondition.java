package org.jgll.grammar.condition;


/**
 * 
 * Conditions relating to the character classes. 
 * For example, Id !>> [0-9] or Id \ [a-z]
 * 
 * All more complicated patterns should be modeled using 
 * context-free conditions.
 * 
 * @author Ali Afroozeh
 *
 */
public class PositionalCondition extends Condition {
	private static final long serialVersionUID = 1L;
	
	public PositionalCondition(ConditionType type) {
		super(type);
	}
	
	@Override
	public String toString() {
		return type.toString();
	}

}
