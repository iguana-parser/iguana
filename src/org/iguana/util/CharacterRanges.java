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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.iguana.regex.CharacterRange;
import static org.iguana.util.CollectionsUtil.*;

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
	
}
