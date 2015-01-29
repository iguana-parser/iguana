package org.jgll.util.collections;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.util.collections.RangeTree.Node;
import org.junit.Test;

public class RangeTreeTest {

	@Test
	public void test() {
		RangeTree<String> rangeTree = new RangeTree<>(CharacterRange.in(1, 4), "a");
		rangeTree.insert(CharacterRange.in(7, 13), "b");
		rangeTree.insert(CharacterRange.in(18, 101), "c");
		
		assertEquals(null, rangeTree.get(0));
		assertEquals("a",  rangeTree.get(1));
		assertEquals("a",  rangeTree.get(3));
		assertEquals("a",  rangeTree.get(4));
		assertEquals(null, rangeTree.get(5));
		assertEquals("b",  rangeTree.get(7));
		assertEquals("b",  rangeTree.get(12));
		assertEquals("b",  rangeTree.get(13));
		assertEquals("c",  rangeTree.get(18));
		assertEquals("c",  rangeTree.get(101));
		assertEquals(null, rangeTree.get(121));
		
		for (Node<String> node : rangeTree) {
			System.out.println(node);
		}
	}
	
}
