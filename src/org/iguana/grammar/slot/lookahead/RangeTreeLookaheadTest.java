package org.iguana.grammar.slot.lookahead;

import iguana.regex.CharRange;
import iguana.utils.collections.rangemap.RangeTree;
import org.iguana.grammar.slot.BodyGrammarSlot;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RangeTreeLookaheadTest implements LookAheadTest {
	
	private final RangeTree<List<BodyGrammarSlot>> rangeTree = new RangeTree<>();

	public RangeTreeLookaheadTest(Map<CharRange, List<BodyGrammarSlot>> nonOverlappingMap) {
		nonOverlappingMap.entrySet().forEach(e -> rangeTree.insert(e.getKey(), e.getValue().isEmpty() ? Collections.emptyList() : e.getValue()));
	}
	
	@Override
	public List<BodyGrammarSlot> get(int v) {
		List<BodyGrammarSlot> alternatives = rangeTree.get(v);
		return alternatives == null ? Collections.emptyList() : alternatives;
	}

}
