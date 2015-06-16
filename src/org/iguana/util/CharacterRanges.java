package org.iguana.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iguana.grammar.symbol.CharacterRange;

public class CharacterRanges {
	
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
