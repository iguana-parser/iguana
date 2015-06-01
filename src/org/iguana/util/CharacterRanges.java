package org.iguana.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.iguana.grammar.symbol.CharacterRange;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

public class CharacterRanges {
	
	public static Multimap<CharacterRange, CharacterRange> toNonOverlapping(CharacterRange...ranges) {
		return toNonOverlapping(Arrays.asList(ranges));
	}
	
	public static Multimap<CharacterRange, CharacterRange> toNonOverlapping(List<CharacterRange> ranges) {
		
		if (ranges.size() == 0)
			return ImmutableListMultimap.of();
		
		if (ranges.size() == 1) 
			return ImmutableListMultimap.of(ranges.get(0), ranges.get(0));
		
		Collections.sort(ranges);

		Multimap<CharacterRange, CharacterRange> result = ArrayListMultimap.create();
		
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
	
	private static Multimap<CharacterRange, CharacterRange> convertOverlapping(Set<CharacterRange> ranges) {
		
		if (ranges.isEmpty())
			return ImmutableListMultimap.of();
		
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
		
		Multimap<CharacterRange, CharacterRange> rangesMap = ArrayListMultimap.create();
		for (CharacterRange r1 : ranges) {
			for (CharacterRange r2 : result) {
				if (r1.contains(r2))
					rangesMap.put(r1, r2);
			}
		}
		
		return rangesMap;
	}
	
}
