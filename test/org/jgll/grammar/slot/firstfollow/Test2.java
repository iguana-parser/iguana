package org.jgll.grammar.slot.firstfollow;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.slot.test.TreeMapPredictionTest;
import org.jgll.grammar.symbol.Range;
import org.junit.Test;

public class Test2 {

	@Test
	public void test1() {
		
		Range r1 = Range.in(1, 3);
		Range r2 = Range.in(5, 7);
		
		TreeMapPredictionTest test = new TreeMapPredictionTest(list(set(r1, r2)), 1);
		
		assertTrue(test.test(1));
		assertTrue(test.test(2));
		assertTrue(test.test(3));
		assertFalse(test.test(4));
		assertTrue(test.test(5));
		assertTrue(test.test(6));
		assertTrue(test.test(7));
		assertFalse(test.test(8));
	}
	
	@Test
	public void test2() {
		
		Range r1 = Range.in(1, 7);
		Range r2 = Range.in(3, 6);
		Range r3 = Range.in(4, 11);
		
		TreeMapPredictionTest test = new TreeMapPredictionTest(list(set(r1, r2), set(r3)), 2);
		
		assertEquals(set(0), test.get(1));
		assertEquals(set(0), test.get(2));
		assertEquals(set(0), test.get(3));
		assertEquals(set(0, 1), test.get(4));
		assertEquals(set(0, 1), test.get(5));
		assertEquals(set(0, 1), test.get(6));
		assertEquals(set(0, 1), test.get(7));
		assertEquals(set(1), test.get(8));
		assertEquals(set(1), test.get(9));
		assertEquals(set(1), test.get(10));
		assertEquals(set(1), test.get(11));
		assertEquals(set(), test.get(12));
	}
	
	
	@Test
	public void test3() {
		
		Range r1 = Range.in(1, 2);
		Range r2 = Range.in(4, 7);
		Range r3 = Range.in(5, 11);
		Range r4 = Range.in(13, 18);
		
		TreeMapPredictionTest test = new TreeMapPredictionTest(list(set(r1), set(r2), set(r3), set(r4)), 4);
	
		assertEquals(set(0), test.get(1));
		assertEquals(set(0), test.get(2));
		assertEquals(set(), test.get(3));
		assertEquals(set(1), test.get(4));
		assertEquals(set(1, 2), test.get(5));
		assertEquals(set(1, 2), test.get(6));
		assertEquals(set(1, 2), test.get(7));
		assertEquals(set(2), test.get(8));
		assertEquals(set(2), test.get(9));
		assertEquals(set(2), test.get(10));
		assertEquals(set(2), test.get(11));
		assertEquals(set(3), test.get(13));
		assertEquals(set(3), test.get(14));
		assertEquals(set(3), test.get(15));
		assertEquals(set(3), test.get(16));
		assertEquals(set(3), test.get(17));
		assertEquals(set(3), test.get(18));
		assertEquals(set(), test.get(19));
		assertEquals(set(), test.get(20));
	}
	
	
}
