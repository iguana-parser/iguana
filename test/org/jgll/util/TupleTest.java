package org.jgll.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TupleTest {

	@Test
	public void testUnOrderedTuple() {
		UnOrderedTuple<Integer, Integer> t1 = new UnOrderedTuple<>(2, 3);
		UnOrderedTuple<Integer, Integer> t2 = new UnOrderedTuple<>(3, 2);
		assertTrue(t1.hashCode() == t2.hashCode());
		assertTrue(t1.equals(t2));
	}
	
	@Test
	public void testOrderedTuple() {
		Tuple<Integer, Integer> t1 = new Tuple<>(2, 3);
		Tuple<Integer, Integer> t2 = new Tuple<>(3, 2);
		assertFalse(t1.hashCode() == t2.hashCode());
		assertTrue(t1.equals(t2));
	}
	
}
