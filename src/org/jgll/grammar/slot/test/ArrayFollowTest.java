package org.jgll.grammar.slot.test;

import java.util.Set;

import org.jgll.grammar.symbol.Range;
import org.jgll.regex.RegularExpression;

import com.google.common.base.Joiner;
import com.google.common.primitives.Booleans;

public class ArrayFollowTest implements FollowTest {

	private static final long serialVersionUID = 1L;

	private final boolean[] followSetMap;
	
	private final int min;
	
	private final int max;
	
	public ArrayFollowTest(boolean[] followSetMap, int min, int max) {
		this.followSetMap = followSetMap;
		this.min = min;
		this.max = max;
	}

	public ArrayFollowTest(Set<RegularExpression> followSet, int min, int max) {
		this.min = min;
		this.max = max;
		followSetMap = new boolean[max - min + 1];
		
		for(RegularExpression regex : followSet) {
			for(Range r : regex.getFirstSet()) {
				for(int v = r.getStart(); v <= r.getEnd(); v++) {
					followSetMap[v - min] = true;
				}
			}
		}
	}
	
	@Override
	public boolean test(int v) {
		if(v < min || v > max) {
			return false;
		}
		return followSetMap[v - min];
	}

	@Override
	public String getConstructorCode() {
		return "new ArrayFollowTest(new boolean[]{" + Joiner.on(",").join(Booleans.asList(followSetMap)) + "})";
	}

}
