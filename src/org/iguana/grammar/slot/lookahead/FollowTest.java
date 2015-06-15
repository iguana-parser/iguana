package org.iguana.grammar.slot.lookahead;

@FunctionalInterface
public interface FollowTest {
	public boolean test(int v);
	
	public static final FollowTest NO_FOLLOW = i -> true;
}
