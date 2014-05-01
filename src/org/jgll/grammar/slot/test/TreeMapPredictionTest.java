package org.jgll.grammar.slot.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jgll.grammar.symbol.Range;
import org.jgll.regex.RegularExpression;

public class TreeMapPredictionTest implements PredictionTest {
	
	private static final long serialVersionUID = 1L;
	
	private NavigableMap<Integer, Set<Integer>> predictionMap;

	public TreeMapPredictionTest(List<? extends Set<? extends RegularExpression>> predictionSets, int countAlternates) {
		predictionMap = new TreeMap<>();
		
		// From range to the set of alternate indices
		Map<Range, Set<Integer>> map = new HashMap<>();
		
		for(int i = 0; i < countAlternates; i++) {
			Set<? extends RegularExpression> predictionSet = predictionSets.get(i);
			
			if(predictionSet.isEmpty()) continue;
			
			for(RegularExpression regex : predictionSet) {
				for(Range r : regex.getFirstSet()) {
					Set<Integer> set = map.get(r);
					if(set == null) {
						set = new HashSet<>();
						map.put(r, set);
					}
					set.add(i);
				}
			}
		}
		
		Set<Integer> starts = new HashSet<>();
		Set<Integer> ends = new HashSet<>();
		
		for(Range r : map.keySet()) {
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
		
		for(Range range : map.keySet()) {
			for(int i : list) {
				if(i >= range.getStart() && i <= range.getEnd()) {
					Set<Integer> s = predictionMap.get(i);
					if(s == null) {
						s = new LinkedHashSet<>();
						predictionMap.put(i, s);
					}
					s.addAll(map.get(range));
					
					if(!predictionMap.containsKey(range.getEnd() + 1)) {
						predictionMap.put(range.getEnd() + 1, new HashSet<Integer>());
					}
				}
			}
		}
	}
	
	@Override
	public boolean test(int v) {
		Entry<Integer, Set<Integer>> e = predictionMap.floorEntry(v);
		return e != null && ! e.getValue().isEmpty();
	}

	@Override
	public Set<Integer> get(int v) {
		Entry<Integer, Set<Integer>> e = predictionMap.floorEntry(v);
		return e == null ? null : e.getValue();
	}

}
