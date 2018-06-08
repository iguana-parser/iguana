package org.iguana.grammar.slot.lookahead;

@FunctionalInterface
public interface FollowTest {
	boolean test(int v);
	
	FollowTest DEFAULT = i -> true;
}
