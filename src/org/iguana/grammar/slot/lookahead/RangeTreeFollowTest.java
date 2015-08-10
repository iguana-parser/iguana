package org.iguana.grammar.slot.lookahead;

import java.util.Set;

import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.util.collections.rangemap.AVLIntRangeTree;
import org.iguana.util.collections.rangemap.ArrayIntRangeTree;
import org.iguana.util.collections.rangemap.IntRangeTree;

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
