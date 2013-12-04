package org.jgll.grammar.symbols;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Range;
import org.junit.Test;


public class RangeTest {
	
	@Test
	public void testContains1() {
		Range range1 = new Range('a', 'z');
		Range range2 = new Range('e', 'h');
		assertTrue(range1.contains(range2));
	}
	
	@Test
	public void testContains2() {
		Range range1 = new Range('a', 'z');
		Range range2 = new Range('a', 'z');
		assertTrue(range1.contains(range2));
	}
	
	@Test
	public void testContains3() {
		Range range1 = new Range('a', 'z');
		Range range2 = new Range('a', 'y');
		assertTrue(range1.contains(range2));
	}

	@Test
	public void testContains4() {
		Range range1 = new Range('c', 'x');
		Range range2 = new Range('a', 'y');
		assertFalse(range1.contains(range2));
	}

}
