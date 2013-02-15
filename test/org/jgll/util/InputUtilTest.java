package org.jgll.util;

import static junit.framework.Assert.*;

import org.junit.Test;

public class InputUtilTest {
	
	@Test
	public void test1() {
		String s = "big\n brother";
		InputUtil inputUtil = InputUtil.inputUtil;
		inputUtil.setInput(s);
		LineColumn lineColumn = inputUtil.getLineNumber(0);
		assertEquals(new LineColumn(1, 1), lineColumn);
		
		lineColumn = inputUtil.getLineNumber(1);
		assertEquals(new LineColumn(1, 2), lineColumn);
		
		lineColumn = inputUtil.getLineNumber(3);
		assertEquals(new LineColumn(1, 4), lineColumn);
		
		lineColumn = inputUtil.getLineNumber(5);
		assertEquals(new LineColumn(2, 2), lineColumn);
	}
	
	@Test
	public void test2() {
		String s = "big\r\n brother";
		InputUtil inputUtil = InputUtil.inputUtil;
		inputUtil.setInput(s);
		LineColumn lineColumn = inputUtil.getLineNumber(0);
		assertEquals(new LineColumn(1, 1), lineColumn);
		
		lineColumn = inputUtil.getLineNumber(1);
		assertEquals(new LineColumn(1, 2), lineColumn);
		
		lineColumn = inputUtil.getLineNumber(3);
		assertEquals(new LineColumn(1, 4), lineColumn);
		
		lineColumn = inputUtil.getLineNumber(6);
		assertEquals(new LineColumn(2, 2), lineColumn);
	}


}
