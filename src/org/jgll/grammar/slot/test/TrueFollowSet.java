package org.jgll.grammar.slot.test;

import org.jgll.grammar.GrammarSlotRegistry;

public class TrueFollowSet implements FollowTest {

	private static final long serialVersionUID = 1L;

	@Override
	public final boolean test(int v) {
		return true;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "new TrueFollowSet()";
	}

}
