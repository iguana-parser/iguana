/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.util;

import static org.junit.Assert.*;

import org.iguana.util.Input;
import org.iguana.util.IntArrayCharSequence;
import org.junit.Test;

public class InputTest {
	
	@Test
	public void testLogicalLine() {
		String s = "\uD83C\uDF55abc\uD83C\uDF55";
		
		IntArrayCharSequence seq = Input.fromString(s).asCharSequence();
		
		assertEquals(5, seq.logicalLength());
		
		assertEquals(0, seq.logicalIndexAt(0));
		assertEquals(0, seq.logicalIndexAt(1));
		assertEquals(1, seq.logicalIndexAt(2));
		assertEquals(2, seq.logicalIndexAt(3));
		assertEquals(3, seq.logicalIndexAt(4));
		assertEquals(4, seq.logicalIndexAt(5));
		assertEquals(4, seq.logicalIndexAt(6));
	}
	
	@Test
	public void testLineColumnNumber1() {
		Input input = Input.fromString("big\n brother");
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
		Input input = Input.fromString("big\r\n brother");

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
		Input input = Input.fromString("We are just another brick in the wall");
		
		assertTrue(input.match(0, "We"));
		assertTrue(input.match(33, "wall"));
		assertFalse(input.match(35, "wall"));
	}
	
	@Test
	public void testMatch2() {
		Input input = Input.fromString("We are just another brick in the wall");
		
		assertTrue(input.match(0, 2, "We"));
		assertTrue(input.match(3, 11, "are just"));
	}
	
	@Test
	public void testMatch3() {
		Input input = Input.fromString("We are just another brick in the wall");
		
		assertTrue(input.matchBackward(2, "We"));
		assertTrue(input.matchBackward(37, "wall"));
		assertTrue(input.matchBackward(37, "all"));
		assertTrue(input.matchBackward(32, "the"));
	
	}
}
