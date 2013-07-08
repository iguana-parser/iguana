package org.jgll.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class InputUtilTest {
	
	private Input input;

	@Test
	public void test1() {
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
	public void test2() {
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


}
