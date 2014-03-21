package org.jgll.grammar.slot.test;

import java.util.HashSet;
import java.util.Set;

public class TruePredictionSet implements PredictionTest {

	private static final long serialVersionUID = 1L;
	
	private final Set<Integer> predictionSet;
	
	public TruePredictionSet(int size) {
		predictionSet = new HashSet<>();
		for(int i = 0; i < size; i++) {
			predictionSet.add(i);
		}
	}

	@Override
	public final boolean test(int v) {
		return true;
	}

	@Override
	public final Set<Integer> get(int v) {
		return predictionSet;
	}
	
}
