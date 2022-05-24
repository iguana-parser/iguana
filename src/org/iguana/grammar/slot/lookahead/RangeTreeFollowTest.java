package org.iguana.grammar.slot.lookahead;

import org.iguana.regex.CharRange;
import org.iguana.utils.collections.rangemap.IntRangeMap;
import org.iguana.utils.collections.rangemap.RangeMapBuilder;

import java.util.Set;

public class RangeTreeFollowTest implements FollowTest {

	private final IntRangeMap rangeMap;
	private final String followTestToString;
	
	public RangeTreeFollowTest(Set<CharRange> set) {
		RangeMapBuilder<Integer> builder = new RangeMapBuilder<>();
		set.forEach(r -> builder.put(r, 1));
		rangeMap = builder.buildIntRangeMap();
		followTestToString = set.toString();
	}
	
	@Override
	public boolean test(int v) {
		return rangeMap.get(v) == 1;
	}

	@Override
	public String toString() {
		return followTestToString;
	}
}
