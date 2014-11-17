package org.jgll.grammar.slot.test;

public class TrueFollowSet implements FollowTest {

	private static final long serialVersionUID = 1L;

	@Override
	public final boolean test(int v) {
		return true;
	}

	@Override
	public String getConstructorCode() {
		return "new TrueFollowSet()";
	}

}
