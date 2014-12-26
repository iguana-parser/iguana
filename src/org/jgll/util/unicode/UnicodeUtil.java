package org.jgll.util.unicode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.CharacterRange;

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
	
	public static CharacterClass getCharacterClassInCategory(String categoryName) {
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
		
		return CharacterClass.from(ranges);
	}
	
	public static boolean isPrintableAscii(int codePoint) {
		return '\u0020' < codePoint && codePoint < '\u007f';
	}
	
	public static CharacterClass reverse(CharacterRange range) {
		List<CharacterRange> ranges = new ArrayList<>();
		if(range.getStart() >= 1) {
			ranges.add(CharacterRange.in(1, range.getStart() - 1));
		}
		if(range.getEnd() < MAX_UTF32_VAL) {
			ranges.add(CharacterRange.in(range.getEnd() + 1, MAX_UTF32_VAL));
		}
		return CharacterClass.from(ranges);
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
