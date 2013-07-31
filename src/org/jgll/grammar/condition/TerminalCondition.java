package org.jgll.grammar.condition;

import java.util.List;

import org.jgll.grammar.Terminal;

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
public class TerminalCondition extends Condition {
	
	private List<Terminal> characterClasses;
	
	public TerminalCondition(ConditionType type, List<Terminal> list) {
		super(type);
		this.characterClasses = list;
	}
	
	public List<Terminal> getTerminals() {
		return characterClasses;
	}
	
	@Override
	public String toString() {
		return type.toString() + " " + listToString(characterClasses);
	}

}
