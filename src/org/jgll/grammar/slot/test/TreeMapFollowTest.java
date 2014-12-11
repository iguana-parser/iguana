package org.jgll.grammar.slot.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.RegularExpression;

import com.google.common.base.Joiner;

public class TreeMapFollowTest implements FollowTest {
	
	private static final long serialVersionUID = 1L;
	
	private final NavigableMap<Integer, Boolean> followMap;
	
	public TreeMapFollowTest(Set<? extends RegularExpression> followSet) {
		followMap = new TreeMap<>();
		
		// From range to the set of alternate indices
		Set<Range> ranges = new HashSet<>();
		
		for(RegularExpression regex : followSet) {
			for(Range r : regex.getFirstSet()) {
				ranges.add(r);
			}
		}
		
		Set<Integer> starts = new HashSet<>();
		Set<Integer> ends = new HashSet<>();
		
		for(Range r : ranges) {
			starts.add(r.getStart());
			ends.add(r.getEnd());
		}
		
		Set<Integer> points = new HashSet<>(starts);
		points.addAll(ends);
		List<Integer> sortedPoints = new ArrayList<>(points);
		Collections.sort(sortedPoints);
		
		Set<Integer> set = new HashSet<>();
		for(int i : sortedPoints) {
			if(starts.contains(i)) {
				set.add(i);
			} 
			if(ends.contains(i)) {
				set.add(i + 1);
			}
		}
		List<Integer> list = new ArrayList<>(set);
		Collections.sort(list);
		
		for(Range range : ranges) {
			for(int i : list) {
				if(i >= range.getStart() && i <= range.getEnd()) {
					followMap.put(i, true);
					if(!followMap.containsKey(range.getEnd() + 1)) {
						followMap.put(range.getEnd() + 1, false);
					}
				}
			}
		}		
	}
	
	
	@Override
	public boolean test(int v) {
		Entry<Integer, Boolean> e = followMap.floorEntry(v);
		return e != null && e.getValue();
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
//		Joiner.MapJoiner
//		Joiner.on(",").join(followMap.entrySet().iterator());
		return null;
	}

}
