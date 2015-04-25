/*
 * Copyright (c) 2015, CWI
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

package org.iguana.util.unicode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.regex.Alt;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class UnicodeUtil {
	
	private static final int MAX_UTF32_VAL = 0x10FFFF;
	
	public static String getTypeName(int codePoint) {
		return categoryNames.get(Character.getType(codePoint));
	}
	
	public static Set<Integer> getValuesInCategory(String categoryName) {
		return categoriesMap.get(categoryName);
	}
	
	public static Alt<CharacterRange> getCharacterClassInCategory(String categoryName) {
		List<Integer> vals = new ArrayList<>(categoriesMap.get(categoryName));
		Collections.sort(vals);

		List<CharacterRange> ranges = new ArrayList<>();
		
		int start = vals.get(0);
		int end = vals.get(0);
		
		for (int i = 1; i < vals.size(); i++) {
			if (vals.get(i) - end > 1) {
				ranges.add(CharacterRange.in(start, end));
				start = vals.get(i);
			}
			end = vals.get(i);
		}
		
		return Alt.from(ranges);
	}
	
	public static boolean isPrintableAscii(int codePoint) {
		return '\u0020' < codePoint && codePoint < '\u007f';
	}
	
	public static Alt<CharacterRange> reverse(CharacterRange range) {
		List<CharacterRange> ranges = new ArrayList<>();
		if(range.getStart() >= 1) {
			ranges.add(CharacterRange.in(1, range.getStart() - 1));
		}
		if(range.getEnd() < MAX_UTF32_VAL) {
			ranges.add(CharacterRange.in(range.getEnd() + 1, MAX_UTF32_VAL));
		}
		return Alt.from(ranges);
	}
	
	private static Map<Byte, String> categoryNames = new HashMap<>();
	
	private static Map<String, Set<Integer>> categoriesMap = new HashMap<>();
	
	static {
	    categoryNames.put(Character.UNASSIGNED, "Cn");

	    categoryNames.put(Character.UPPERCASE_LETTER, "Lu");

	    categoryNames.put(Character.LOWERCASE_LETTER, "Ll");

	    categoryNames.put(Character.TITLECASE_LETTER, "Lt");

	    categoryNames.put(Character.MODIFIER_LETTER, "Lm");

	    categoryNames.put(Character.OTHER_LETTER, "Lo");

	    categoryNames.put(Character.NON_SPACING_MARK, "Mn");

	    categoryNames.put(Character.ENCLOSING_MARK, "Me");
	    
	    categoryNames.put(Character.COMBINING_SPACING_MARK, "Mc");

	    categoryNames.put(Character.DECIMAL_DIGIT_NUMBER, "Nd");

	    categoryNames.put(Character.LETTER_NUMBER, "Nl");

	    categoryNames.put(Character.OTHER_NUMBER, "No");

	    categoryNames.put(Character.SPACE_SEPARATOR, "Zs");

	    categoryNames.put(Character.LINE_SEPARATOR, "Zl");

	    categoryNames.put(Character.PARAGRAPH_SEPARATOR, "Zp");

	    categoryNames.put(Character.CONTROL, "Cc");

	    categoryNames.put(Character.FORMAT, "Cf");

	    categoryNames.put(Character.PRIVATE_USE, "Co");

	    categoryNames.put(Character.SURROGATE, "Cs");

	    categoryNames.put(Character.DASH_PUNCTUATION, "Pd");

	    categoryNames.put(Character.START_PUNCTUATION, "Ps");

	    categoryNames.put(Character.END_PUNCTUATION, "Pe");

	    categoryNames.put(Character.CONNECTOR_PUNCTUATION, "Pc");

	    categoryNames.put(Character.OTHER_PUNCTUATION, "Po");

	    categoryNames.put(Character.MATH_SYMBOL, "Sm");

	    categoryNames.put(Character.CURRENCY_SYMBOL, "Sc");

	    categoryNames.put(Character.MODIFIER_SYMBOL, "Sk");

	    categoryNames.put(Character.OTHER_SYMBOL, "So");

	    categoryNames.put(Character.INITIAL_QUOTE_PUNCTUATION, "Pi");

	    categoryNames.put(Character.FINAL_QUOTE_PUNCTUATION, "Pf");
	    
	    for (int i = 0; i <= MAX_UTF32_VAL; i++) {
	    	String categoryName = categoryNames.get((byte) Character.getType(i));
			Set<Integer> set = categoriesMap.get(categoryName);
			if (set == null) {
				set = new HashSet<>();
				categoriesMap.put(categoryName, set);
			}
			set.add(i);
	    }
	}
	
}
