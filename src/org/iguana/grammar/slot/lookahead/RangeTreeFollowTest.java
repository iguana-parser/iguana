package org.iguana.grammar.slot.lookahead;

import java.util.Set;

import org.iguana.grammar.symbol.CharacterRange;
import iguana.utils.collections.rangemap.AVLIntRangeTree;
import iguana.utils.collections.rangemap.ArrayIntRangeTree;
import iguana.utils.collections.rangemap.IntRangeTree;

public class RangeTreeFollowTest implements FollowTest {

	private IntRangeTree rangeTree;
	
	public RangeTreeFollowTest(Set<CharacterRange> set) {
		rangeTree = new AVLIntRangeTree();
		set.forEach(r -> rangeTree.insert(r, 1));
		rangeTree = new ArrayIntRangeTree(rangeTree);
	}
	
	@Override
	public boolean test(int v) {
		return rangeTree.get(v) == 1;
	}

}
