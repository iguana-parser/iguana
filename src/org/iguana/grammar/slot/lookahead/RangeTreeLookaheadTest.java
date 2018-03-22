package org.iguana.grammar.slot.lookahead;

import iguana.regex.CharRange;
import iguana.utils.collections.rangemap.RangeTree;
import org.iguana.grammar.slot.BodyGrammarSlot;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class RangeTreeLookaheadTest<T> implements LookAheadTest<T> {
	
	private final RangeTree<List<BodyGrammarSlot<T>>> rangeTree = new RangeTree<>();

	public RangeTreeLookaheadTest(Map<CharRange, List<BodyGrammarSlot<T>>> nonOverlappingMap) {
		nonOverlappingMap.forEach((key, value) -> rangeTree.insert(key, value.isEmpty() ? emptyList() : value));
	}
	
	@Override
	public List<BodyGrammarSlot<T>> get(int v) {
		List<BodyGrammarSlot<T>> alternatives = rangeTree.get(v);
		return alternatives == null ? emptyList() : alternatives;
	}

}
