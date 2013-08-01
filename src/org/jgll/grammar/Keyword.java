package org.jgll.grammar;

import java.util.Arrays;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;
import org.jgll.util.Input;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.HashFunction;

public class Keyword extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	public static final ExternalHasher<Keyword> externalHasher = new KeywordExternalHasher();
	
	private final int[] chars;
	
	private final String name;
	
	public Keyword(String name, String s) {
		this.chars = Input.toIntArray(s);
		this.name = name;
	}
	
	public Keyword(String name, int[] chars) {
		this.chars = chars;
		this.name = name;
	}
	
	public int[] getChars() {
		return chars;
	}

	public int size() {
		return chars.length;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Terminal getFirstTerminal() {
		return new Character(chars[0]);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Keyword)) {
			return false;
		}
		
		Keyword other = (Keyword) obj;
		
		return Arrays.equals(chars, other.chars);
	}
	
	@Override
	public int hashCode() {
		return externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	public static class KeywordExternalHasher implements ExternalHasher<Keyword> {

		@Override
		public int hash(Keyword k, HashFunction f) {
			return f.hash(k.getChars());
		}
	}

	@Override
	public Keyword addCondition(Condition condition) {
		Keyword keyword = new Keyword(this.name, this.chars);
		keyword.conditions.addAll(conditions);
		keyword.conditions.add(condition);
		return keyword;
	}
	
}
