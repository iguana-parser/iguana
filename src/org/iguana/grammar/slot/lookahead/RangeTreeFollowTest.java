package org.iguana.grammar.slot.lookahead;

import iguana.regex.CharRange;
import iguana.utils.collections.rangemap.IntRangeMap;
import iguana.utils.collections.rangemap.RangeMapBuilder;

import java.util.Set;

public class RangeTreeFollowTest implements FollowTest {

	private IntRangeMap rangeMap;
	
	public RangeTreeFollowTest(Set<CharRange> set) {
		RangeMapBuilder<Integer> builder = new RangeMapBuilder<>();
		set.forEach(r -> builder.put(r, 1));
		rangeMap = builder.buildIntRangeMap();
	}
	
	@Override
	public boolean test(int v) {
		return rangeMap.get(v) == 1;
	}

}
