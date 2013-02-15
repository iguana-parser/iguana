package org.jgll.util;

import static junit.framework.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class InputUtilTest {
	
	private InputUtil inputUtil;

	@Before
	public void init() {
		inputUtil = InputUtil.getInstance();
	}
	
	@Test
	public void test1() {
		inputUtil.setInput("big\n brother");
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
		inputUtil.setInput("big\r\n brother");
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
