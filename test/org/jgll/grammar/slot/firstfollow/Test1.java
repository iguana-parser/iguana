package org.jgll.grammar.slot.firstfollow;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Range;
import org.junit.Test;

public class Test1 {

	
	@Test
	public void test1() {
		
		Range r1 = Range.in(1, 3);
		Range r2 = Range.in(5, 7);
		
		TreeMapFollowTest test = new TreeMapFollowTest(set(r1, r2));
		
		assertTrue(test.test(1));
		assertTrue(test.test(2));
		assertTrue(test.test(3));
		assertFalse(test.test(4));
		assertTrue(test.test(5));
		assertTrue(test.test(6));
		assertTrue(test.test(7));
		assertTrue(test.test(8));
	}
	
}
