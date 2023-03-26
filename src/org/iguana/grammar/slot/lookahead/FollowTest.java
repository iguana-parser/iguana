package org.iguana.grammar.slot.lookahead;

@FunctionalInterface
public interface FollowTest {
    FollowTest DEFAULT = i -> true;

    boolean test(int v);
}
