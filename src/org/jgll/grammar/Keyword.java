package org.jgll.grammar;

import java.util.ArrayList;
import java.util.List;

import org.jgll.util.Input;

public class Keyword implements Symbol {

	private static final long serialVersionUID = 1L;
	
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
	
	public boolean match(int c) {
		return chars[0] == c;
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
	
	@Override
	public String toString() {
		return getName();
	}
	
}
