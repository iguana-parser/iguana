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
package iguana.regex;

import java.lang.*;
import java.util.*;
import java.util.stream.Collectors;

import static iguana.utils.collections.CollectionsUtil.toList;

public class CharacterRanges {

	public static Set<CharacterRange> toNonOverlappingSet(Iterable<CharacterRange> ranges) {
		return toNonOverlapping(toList(ranges)).values().stream().flatMap(l -> l.stream()).collect(Collectors.toSet());
	}

    /**
	 * 
	 * @return a map from the new, non-overlapping ranges to original ranges
	 */
	public static Map<CharacterRange, List<CharacterRange>> toNonOverlapping2(Iterable<CharacterRange> ranges) {
		Map<CharacterRange, List<CharacterRange>> nonOverlapping = toNonOverlapping(toList(ranges));
		Map<CharacterRange, List<CharacterRange>> map = new HashMap<>();
		nonOverlapping.forEach((k, v) -> v.forEach(r -> map.computeIfAbsent(r, key -> new ArrayList<>()).add(k)));
		return map;
	}

	
	public static Map<CharacterRange, List<CharacterRange>> toNonOverlapping(Iterable<CharacterRange> ranges) {
		return toNonOverlapping(toList(ranges));
	}
	
	public static Map<CharacterRange, List<CharacterRange>> toNonOverlapping(CharacterRange...ranges) {
		return toNonOverlapping(Arrays.asList(ranges));
	}
	
	public static Map<CharacterRange, List<CharacterRange>> toNonOverlapping(List<CharacterRange> ranges) {
		
		if (ranges.size() == 0)
			return Collections.emptyMap();
		
		if (ranges.size() == 1) {
			Map<CharacterRange, List<CharacterRange>> map = new HashMap<>();
			map.computeIfAbsent(ranges.get(0), k -> new ArrayList<>()).add(ranges.get(0));
			return map;
		}
		
		Collections.sort(ranges);

		Map<CharacterRange, List<CharacterRange>> result = new HashMap<>();
		
		Set<CharacterRange> overlapping = new HashSet<>();
		
		for (int i = 0; i < ranges.size(); i++) {
			
			CharacterRange current = ranges.get(i);
			overlapping.add(current);

			if (i + 1 < ranges.size()) {
				CharacterRange next = ranges.get(i + 1);
				if (!overlaps(overlapping, next)) {
					result.putAll(convertOverlapping(overlapping));
					overlapping.clear();
				}
			}
		}
		
		result.putAll(convertOverlapping(overlapping));
		
		return result;
	}
	
	private static boolean overlaps(Set<CharacterRange> set, CharacterRange r) {
		for (CharacterRange c : set) {
			if (c.overlaps(r)) {
				return true;
			}
		}
		return false;
	}
	
	private static Map<CharacterRange, List<CharacterRange>> convertOverlapping(Set<CharacterRange> ranges) {
		
		if (ranges.isEmpty())
			return Collections.emptyMap();
		
		Set<Integer> set = new HashSet<>();
		for (CharacterRange r : ranges) {
			set.add(r.getStart() - 1);
			set.add(r.getEnd());
		}
		List<Integer> l = new ArrayList<>(set);
		Collections.sort(l);
		
		List<CharacterRange> result = new ArrayList<>();
		
		int start = l.get(0) + 1;
		for (int i = 1; i < l.size(); i++) {
			result.add(CharacterRange.in(start, l.get(i)));
			start = l.get(i) + 1;
		}
		
		Map<CharacterRange, List<CharacterRange>> rangesMap = new HashMap<>();
		for (CharacterRange r1 : ranges) {
			for (CharacterRange r2 : result) {
				if (r1.contains(r2))
					rangesMap.computeIfAbsent(r1, k -> new ArrayList<>()).add(r2);
			}
		}
		
		return rangesMap;
	}

    public static final int MAX_UTF32_VAL = 0x10FFFF;

    public static String getTypeName(int codePoint) {
        return categoryNames.get(java.lang.Character.getType(codePoint));
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
        categoryNames.put(java.lang.Character.UNASSIGNED, "Cn");

        categoryNames.put(java.lang.Character.UPPERCASE_LETTER, "Lu");

        categoryNames.put(java.lang.Character.LOWERCASE_LETTER, "Ll");

        categoryNames.put(java.lang.Character.TITLECASE_LETTER, "Lt");

        categoryNames.put(java.lang.Character.MODIFIER_LETTER, "Lm");

        categoryNames.put(java.lang.Character.OTHER_LETTER, "Lo");

        categoryNames.put(java.lang.Character.NON_SPACING_MARK, "Mn");

        categoryNames.put(java.lang.Character.ENCLOSING_MARK, "Me");

        categoryNames.put(java.lang.Character.COMBINING_SPACING_MARK, "Mc");

        categoryNames.put(java.lang.Character.DECIMAL_DIGIT_NUMBER, "Nd");

        categoryNames.put(java.lang.Character.LETTER_NUMBER, "Nl");

        categoryNames.put(java.lang.Character.OTHER_NUMBER, "No");

        categoryNames.put(java.lang.Character.SPACE_SEPARATOR, "Zs");

        categoryNames.put(java.lang.Character.LINE_SEPARATOR, "Zl");

        categoryNames.put(java.lang.Character.PARAGRAPH_SEPARATOR, "Zp");

        categoryNames.put(java.lang.Character.CONTROL, "Cc");

        categoryNames.put(java.lang.Character.FORMAT, "Cf");

        categoryNames.put(java.lang.Character.PRIVATE_USE, "Co");

        categoryNames.put(java.lang.Character.SURROGATE, "Cs");

        categoryNames.put(java.lang.Character.DASH_PUNCTUATION, "Pd");

        categoryNames.put(java.lang.Character.START_PUNCTUATION, "Ps");

        categoryNames.put(java.lang.Character.END_PUNCTUATION, "Pe");

        categoryNames.put(java.lang.Character.CONNECTOR_PUNCTUATION, "Pc");

        categoryNames.put(java.lang.Character.OTHER_PUNCTUATION, "Po");

        categoryNames.put(java.lang.Character.MATH_SYMBOL, "Sm");

        categoryNames.put(java.lang.Character.CURRENCY_SYMBOL, "Sc");

        categoryNames.put(java.lang.Character.MODIFIER_SYMBOL, "Sk");

        categoryNames.put(java.lang.Character.OTHER_SYMBOL, "So");

        categoryNames.put(java.lang.Character.INITIAL_QUOTE_PUNCTUATION, "Pi");

        categoryNames.put(java.lang.Character.FINAL_QUOTE_PUNCTUATION, "Pf");

        for (int i = 0; i <= MAX_UTF32_VAL; i++) {
            String categoryName = categoryNames.get((byte) java.lang.Character.getType(i));
            Set<Integer> set = categoriesMap.get(categoryName);
            if (set == null) {
                set = new HashSet<>();
                categoriesMap.put(categoryName, set);
            }
            set.add(i);
        }
    }
	
}
