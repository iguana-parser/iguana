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
package org.iguana.regex;

import java.util.*;

import static org.iguana.utils.collections.CollectionsUtil.toList;

public class CharacterRanges {

	public static Map<CharRange, List<CharRange>> toNonOverlapping(Iterable<CharRange> ranges) {
		return toNonOverlapping(toList(ranges));
	}
	
	public static Map<CharRange, List<CharRange>> toNonOverlapping(CharRange...ranges) {
		return toNonOverlapping(Arrays.asList(ranges));
	}
	
	public static Map<CharRange, List<CharRange>> toNonOverlapping(List<CharRange> ranges) {
		
		if (ranges.size() == 0)
			return Collections.emptyMap();
		
		if (ranges.size() == 1) {
			Map<CharRange, List<CharRange>> map = new HashMap<>();
			map.computeIfAbsent(ranges.get(0), k -> new ArrayList<>()).add(ranges.get(0));
			return map;
		}
		
		Collections.sort(ranges);

		Map<CharRange, List<CharRange>> result = new HashMap<>();
		
		Set<CharRange> overlapping = new HashSet<>();
		
		for (int i = 0; i < ranges.size(); i++) {
			
			CharRange current = ranges.get(i);
			overlapping.add(current);

			if (i + 1 < ranges.size()) {
				CharRange next = ranges.get(i + 1);
				if (!overlaps(overlapping, next)) {
					result.putAll(convertOverlapping(overlapping));
					overlapping.clear();
				}
			}
		}
		
		result.putAll(convertOverlapping(overlapping));
		
		return result;
	}
	
	private static boolean overlaps(Set<CharRange> set, CharRange r) {
		for (CharRange c : set) {
			if (c.overlaps(r)) {
				return true;
			}
		}
		return false;
	}
	
	private static Map<CharRange, List<CharRange>> convertOverlapping(Set<CharRange> ranges) {
		
		if (ranges.isEmpty())
			return Collections.emptyMap();
		
		Set<Integer> set = new HashSet<>();
		for (CharRange r : ranges) {
			set.add(r.getStart() - 1);
			set.add(r.getEnd());
		}
		List<Integer> l = new ArrayList<>(set);
		Collections.sort(l);
		
		List<CharRange> result = new ArrayList<>();
		
		int start = l.get(0) + 1;
		for (int i = 1; i < l.size(); i++) {
			result.add(CharRange.in(start, l.get(i)));
			start = l.get(i) + 1;
		}
		
		Map<CharRange, List<CharRange>> rangesMap = new HashMap<>();
		for (CharRange r1 : ranges) {
			for (CharRange r2 : result) {
				if (r1.contains(r2))
					rangesMap.computeIfAbsent(r1, k -> new ArrayList<>()).add(r2);
			}
		}
		
		return rangesMap;
	}

    public static final int MAX_UTF32_VAL = 0x10FFFF;

    public static boolean isPrintableAscii(int codePoint) {
        return '\u0020' < codePoint && codePoint < '\u007f';
    }

    public static Alt<CharRange> reverse(CharRange range) {
        List<CharRange> ranges = new ArrayList<>();
        if(range.getStart() >= 1) {
            ranges.add(CharRange.in(1, range.getStart() - 1));
        }
        if(range.getEnd() < MAX_UTF32_VAL) {
            ranges.add(CharRange.in(range.getEnd() + 1, MAX_UTF32_VAL));
        }
        return Alt.from(ranges);
    }

}
