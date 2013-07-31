package org.jgll.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgll.parser.HashFunctions;
import org.jgll.util.Input;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.HashFunction;

public class Keyword extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	public static final ExternalHasher<Keyword> externalHasher = new KeywordExternalHasher();
	
	private int[] chars;
	
	public Keyword(String s) {
		this.chars = Input.toIntArray(s);
	}
	
	public Keyword(int[] chars) {
		this.chars = chars;
	}
	
	public int[] getChars() {
		return chars;
	}

	public int size() {
		return chars.length;
	}
	
	@Override
	public String getName() {
		List<java.lang.Character> charList = new ArrayList<>();
		for(int i : chars) {
			char[] chars = java.lang.Character.toChars(i);
			for(char c : chars) {
				charList.add(c);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for(char c : charList) {
			sb.append(c);
		}
		
		return sb.toString();
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
	
}
