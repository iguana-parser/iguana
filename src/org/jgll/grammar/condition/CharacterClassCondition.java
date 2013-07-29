package org.jgll.grammar.condition;

import java.util.List;

import org.jgll.grammar.CharacterClass;

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
public class CharacterClassCondition extends Condition {
	
	private List<CharacterClass> characterClasses;
	
	public CharacterClassCondition(ConditionType type, List<CharacterClass> list) {
		super(type);
		this.characterClasses = list;
	}
	
	public List<CharacterClass> getCharacterClasses() {
		return characterClasses;
	}
	
	@Override
	public String toString() {
		return type.toString() + " " + listToString(characterClasses);
	}

}
