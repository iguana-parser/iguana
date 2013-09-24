package org.jgll.grammar.symbols;

import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;

/**
 * 
 * A regular list defines a deterministic, longest-match list of 
 * characters. Regular lists are of the form [x]+ or [x]*, where
 * x is a character class. 
 * 
 * @author Ali Afroozeh
 *
 */
public class RegularList extends AbstractSymbol {

	private static final long serialVersionUID = 1L;

	private final int minimum;
	
	private final CharacterClass characterClass;
	
	private Object object;
	
	private final String name;

	public static RegularList plus(String name, CharacterClass characterClass) {
		return new RegularList(1, characterClass, name);
	}
	
	public static RegularList plus(String name, CharacterClass characterClass, Object object) {
		RegularList regularList = new RegularList(1, characterClass, name);
		regularList.object = object;
		return regularList;
	}
	
	public static RegularList star(String name, CharacterClass characterClass) {
		return new RegularList(0, characterClass, name);
	}
	
	public static RegularList star(String name, CharacterClass characterClass, Object object) {
		RegularList regularList = new RegularList(0, characterClass, name);
		regularList.object = object;
		return regularList;
	}	
	
	private RegularList(int minimum, CharacterClass characterClass, String name) {
		this.minimum = minimum;
		this.characterClass = characterClass;
		this.name = name;
	}
	
	public CharacterClass getCharacterClass() {
		return characterClass;
	}
	
	public int getMinimum() {
		return minimum;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Object getObject() {
		return object;
	}

	@Override
	public RegularList addConditions(Collection<Condition> conditions) {
		if(minimum == 0) {
			RegularList regularList = RegularList.star(name, characterClass);
			regularList.conditions.addAll(this.conditions);
			regularList.conditions.addAll(conditions);
			return regularList;
		} else {
			RegularList regularList = RegularList.plus(name, characterClass);
			regularList.conditions.addAll(this.conditions);
			regularList.conditions.addAll(conditions);
			return regularList;			
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof RegularList)) {
			return false;
		}
		
		RegularList other = (RegularList) obj;
		
		return characterClass.equals(other.characterClass) && minimum == other.minimum;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(characterClass.hashCode(), minimum);
	}
}
