package org.iguana.grammar.slot.lookahead;

import iguana.regex.CharRange;
import iguana.utils.collections.rangemap.RangeTree;
import org.iguana.grammar.slot.BodyGrammarSlot;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class RangeTreeLookaheadTest implements LookAheadTest {
	
	private final RangeTree<List<BodyGrammarSlot>> rangeTree = new RangeTree<>();

	public RangeTreeLookaheadTest(Map<CharRange, List<BodyGrammarSlot>> nonOverlappingMap) {
		nonOverlappingMap.forEach((key, value) -> rangeTree.insert(key, value.isEmpty() ? emptyList() : value));
	}
	
	@Override
	public List<BodyGrammarSlot> get(int v) {
		List<BodyGrammarSlot> alternatives = rangeTree.get(v);
		return alternatives == null ? emptyList() : alternatives;
	}

}
