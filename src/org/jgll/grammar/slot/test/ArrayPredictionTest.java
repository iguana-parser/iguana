package org.jgll.grammar.slot.test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.Range;
import org.jgll.regex.RegularExpression;

public class ArrayPredictionTest implements PredictionTest {

	private static final long serialVersionUID = 1L;
	
	/**
	 * A mapping from an input value to a set of alternatives of this nonterminal
	 * that can be predicted for that input value.
	 */
	private final Set<Integer>[] predictionMap;

	private final int min;

	private final int max;
	
	@SuppressWarnings("unchecked")
	public ArrayPredictionTest(List<Set<RegularExpression>> predictionSets, int min, int max) {
			
		this.min = min;
		this.max = max;
		predictionMap = new Set[max - min + 1];
		
		for(int i = predictionSets.size() - 1 ; i >= 0; i--) {
			Set<RegularExpression> predictionSet = predictionSets.get(i);
			
			if(predictionSet.isEmpty()) continue;
			
			for(RegularExpression regex : predictionSet) {
				for(Range r : regex.getFirstSet()) {
					for(int v = r.getStart(); v <= r.getEnd(); v++) {
						Set<Integer> set = predictionMap[v - min];
						if(set == null) {
							set = new LinkedHashSet<>();
							predictionMap[v - min] = set;
						}
						set.add(i);
					}
				}
			}
		}
	}
	
	@Override
	public boolean test(int v) {
		if(v < min || v > max) {
			return false;
		}
		return predictionMap[v - min] != null;
	}
	
	@Override
	public Set<Integer> get(int v) {
		return predictionMap[v - min];
	}

}
