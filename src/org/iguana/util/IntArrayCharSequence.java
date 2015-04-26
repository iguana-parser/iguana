/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntArrayCharSequence implements CharSequence {
	
	private final int[] input;

	private final char[] chars;

	private final int[] indices;
	
	private final int logicalLength;
	
	public IntArrayCharSequence(int[] input) {
		this.input = input;
		List<Integer> indicesList = new ArrayList<>();
		List<Character> charsList = new ArrayList<>();
		
		int logicalIndex = 0;
		
		for (int i = 0; i < input.length; i++) {
			if (Character.isBmpCodePoint(input[i])) {
				charsList.add((char) input[i]);
				indicesList.add(logicalIndex++);
			} else {
				char[] arr = Character.toChars(input[i]);
				charsList.add(arr[0]);
				charsList.add(arr[1]);
				indicesList.add(logicalIndex);
				indicesList.add(logicalIndex++);
			}
		}
		
		this.logicalLength = logicalIndex; 
		
		this.indices = new int[indicesList.size()];
		this.chars = new char[charsList.size()];
		for (int i = 0; i < indicesList.size(); i++) {
			indices[i] = indicesList.get(i);
			chars[i] = charsList.get(i);
		}
	}
	
	public int logicalLength() {
		return logicalLength;
	}
	
	public int logicalIndexAt(int index) {
		return indices[index];
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
		int[] dest = new int[end - start + 1];
		System.arraycopy(input, start, dest, end, end - start + 1);
		return new IntArrayCharSequence(dest);
	}
	
	@Override
	public String toString() {
		return new String(chars);
	}

}
