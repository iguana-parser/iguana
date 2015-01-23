package org.jgll.util;

import java.util.ArrayList;
import java.util.List;

public class IntArrayCharSequence implements CharSequence {
	
	private char[] chars;

	private int[] indices;

	public IntArrayCharSequence(int[] chars) {
		List<Integer> indicesList = new ArrayList<>();
		List<Character> charsList = new ArrayList<>();
		
		for (int i = 0; i < chars.length; i++) {
			if (Character.isBmpCodePoint(chars[i])) {
				charsList.add((char) chars[i]);
				indicesList.add(i);
			} else {
				charsList.add((char) (chars[i] & 0xffff));
				indicesList.add(i);
				indicesList.add(i);
			}
		}
		
		this.indices = new int[indicesList.size()];
		this.chars = new char[charsList.size()];
		for (int i = 0; i < indicesList.size(); i++) {
			indices[i] = indicesList.get(i);
			chars[i] = charsList.get(i);
		}
	}

	@Override
	public int length() {
		return chars.length;
	}

	@Override
	public char charAt(int index) {
		return chars[index];
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return null;
	}

}
