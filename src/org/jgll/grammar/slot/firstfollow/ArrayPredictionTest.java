package org.jgll.grammar.slot.firstfollow;

import java.util.HashSet;
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
	private Set<Integer>[] predictionMap;

	private int min;

	private int max;
	
	@SuppressWarnings("unchecked")
	public ArrayPredictionTest(List<Set<RegularExpression>> predictionSets, int countAlternates, int min, int max) {
			
		this.min = min;
		this.max = max;
		predictionMap = new Set[max - min + 1];
		
		for(int i = 0; i < countAlternates; i++) {
			Set<RegularExpression> predictionSet = predictionSets.get(i);
			
			if(predictionSet.isEmpty()) continue;
			
			for(RegularExpression regex : predictionSet) {
				for(Range r : regex.getFirstSet()) {
					for(int v = r.getStart(); v <= r.getEnd(); v++) {
						Set<Integer> set = predictionMap[v - min];
						if(set == null) {
							set = new HashSet<>();
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
