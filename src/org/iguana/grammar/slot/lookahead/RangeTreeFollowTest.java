package org.iguana.grammar.slot.lookahead;

import java.util.Set;

import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.util.collections.rangemap.AVLIntRangeTree;

public class RangeTreeFollowTest implements FollowTest {

	private final AVLIntRangeTree rangeTree = new AVLIntRangeTree();
	
	public RangeTreeFollowTest(Set<CharacterRange> set) {
		set.forEach(r -> rangeTree.insert(r, 1));
	}
	
	@Override
	public boolean test(int v) {
		return rangeTree.get(v) == 1;
	}

}
