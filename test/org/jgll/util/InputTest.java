package org.jgll.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class InputTest {
	
	private Input input;

	@Test
	public void testLineColumnNumber1() {
		input = Input.fromString("big\n brother");
		assertEquals(1, input.getLineNumber(0));
		assertEquals(1, input.getColumnNumber(0));
		
		assertEquals(1, input.getLineNumber(1));
		assertEquals(2, input.getColumnNumber(1));
		
		assertEquals(1, input.getLineNumber(3));
		assertEquals(4, input.getColumnNumber(3));
		
		assertEquals(2, input.getLineNumber(5));
		assertEquals(2, input.getColumnNumber(5));
	}
	
	@Test
	public void testLineColumnNumber2() {
		input = Input.fromString("big\r\n brother");

		assertEquals(1, input.getLineNumber(0));
		assertEquals(1, input.getColumnNumber(0));

		assertEquals(1, input.getLineNumber(1));
		assertEquals(2, input.getColumnNumber(1));
		
		assertEquals(1, input.getLineNumber(3));
		assertEquals(4, input.getColumnNumber(3));
		
		assertEquals(2, input.getLineNumber(6));
		assertEquals(2, input.getColumnNumber(6));
	}
	
	@Test
	public void testMatch1() {
		input = Input.fromString("We are just another brick in the wall");
		
		assertTrue(input.match(0, "We"));
		assertTrue(input.match(33, "wall"));
		assertFalse(input.match(35, "wall"));
	}
	
	@Test
	public void testMatch2() {
		input = Input.fromString("We are just another brick in the wall");
		
		assertTrue(input.match(3, 11, "are just"));
		assertTrue(input.match(0, 2, "We"));
	}
	
	@Test
	public void testMatch3() {
		input = Input.fromString("We are just another brick in the wall");
		
		assertTrue(input.matchBackward(2, "We"));
		assertTrue(input.matchBackward(37, "wall"));
		assertTrue(input.matchBackward(37, "all"));
		assertTrue(input.matchBackward(32, "the"));
	
	}
}
