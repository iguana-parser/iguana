package org.iguana.grammar.slot.lookahead;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.util.collections.RangeTree;

public class RangeTreeLookaheadTest implements LookAheadTest {
	
	private final RangeTree<List<BodyGrammarSlot>> rangeTree = new RangeTree<>();

	public RangeTreeLookaheadTest(Map<CharacterRange, List<BodyGrammarSlot>> nonOverlappingMap) {
//		nonOverlappingMap.entrySet().forEach(e -> rangeTree.insert(e.getKey(), e.getValue().isEmpty() ? Collections.emptyList() : e.getValue()));
		for (Entry<CharacterRange, List<BodyGrammarSlot>> e : nonOverlappingMap.entrySet()) {
			System.out.println(e.getKey());
			rangeTree.insert(e.getKey(), e.getValue());
		}
	}
	
	@Override
	public List<BodyGrammarSlot> get(int v) {
		List<BodyGrammarSlot> alternatives = rangeTree.get(v);
		return alternatives == null ? Collections.emptyList() : alternatives;
	}

}
