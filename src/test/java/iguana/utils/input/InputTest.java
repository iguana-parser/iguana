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

package iguana.utils.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InputTest {

    private Input input1;
    private Input input2;
    private Input input3;
    private Input input4;
    private Input input5;
    private Input input6;

    @BeforeEach
    public void init() {
        input1 = Input.empty();
        input2 = Input.fromString("big\n brother");
        input3 = Input.fromString("big\r\n brother");

        String[] lines = {
                "a",
                "bc",
                "defg",
                "hklm",
                "nopqrstu",
                "vwxyz0",
                "123"
        };

        input4 = Input.fromString(String.join("\n", lines));
        input5 = Input.fromString("We are just another brick in the wall");
        input6 = Input.fromString("🍕\n\n🥦🦁a🍆\n🐰");

    }

    @Test
    public void lengthTest() {
        assertEquals(1, input1.length());
        assertEquals(13, input2.length());
        assertEquals(14, input3.length());
        assertEquals(35, input4.length());
        assertEquals(38, input5.length());
        assertEquals(10, input6.length());
    }

    @Test
	public void testLineColumnNumber1() {
		assertEquals(1, input2.getLineNumber(0));
		assertEquals(1, input2.getColumnNumber(0));
		
		assertEquals(1, input2.getLineNumber(1));
		assertEquals(2, input2.getColumnNumber(1));
		
		assertEquals(1, input2.getLineNumber(3));
		assertEquals(4, input2.getColumnNumber(3));
		
		assertEquals(2, input2.getLineNumber(5));
		assertEquals(2, input2.getColumnNumber(5));
	}
	
	@Test
	public void testLineColumnNumber2() {
		assertEquals(1, input3.getLineNumber(0));
		assertEquals(1, input3.getColumnNumber(0));

		assertEquals(1, input3.getLineNumber(1));
		assertEquals(2, input3.getColumnNumber(1));
		
		assertEquals(1, input3.getLineNumber(3));
		assertEquals(4, input3.getColumnNumber(3));
		
		assertEquals(2, input3.getLineNumber(6));
		assertEquals(2, input3.getColumnNumber(6));
	}

	@Test
	public void testLineColumnNumber3() {
        assertEquals(1, input4.getLineNumber(0));
        assertEquals(1, input4.getColumnNumber(0));

        assertEquals(1, input4.getLineNumber(1));
        assertEquals(2, input4.getColumnNumber(1));

        assertEquals(2, input4.getLineNumber(2));
        assertEquals(1, input4.getColumnNumber(2));

        assertEquals(3, input4.getLineNumber(5));
        assertEquals(1, input4.getColumnNumber(5));

        assertEquals(4, input4.getLineNumber(10));
        assertEquals(1, input4.getColumnNumber(10));

        assertEquals(4, input4.getLineNumber(12));
        assertEquals(3, input4.getColumnNumber(12));

        assertEquals(5, input4.getLineNumber(15));
        assertEquals(1, input4.getColumnNumber(15));

        assertEquals(5, input4.getLineNumber(22));
        assertEquals(8, input4.getColumnNumber(22));

        assertEquals(6, input4.getLineNumber(24));
        assertEquals(1, input4.getColumnNumber(24));

        assertEquals(6, input4.getLineNumber(30));
        assertEquals(7, input4.getColumnNumber(30));

        assertEquals(7, input4.getLineNumber(31));
        assertEquals(1, input4.getColumnNumber(31));

        assertEquals(7, input4.getLineNumber(34));
        assertEquals(4, input4.getColumnNumber(34));
    }

    @Test
    public void testBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> input4.getLineNumber(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.getColumnNumber(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.isStartOfLine(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.isEndOfLine(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.isEndOfFile(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.charAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.getLineNumber(35));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.getColumnNumber(35));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.isStartOfLine(35));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.isEndOfLine(35));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.isEndOfFile(35));
        assertThrows(IndexOutOfBoundsException.class, () -> input4.charAt(35));
    }

    @Test
    public void testIsStartOfLine() {
        assertTrue(input4.isStartOfLine(0));
        assertTrue(input4.isStartOfLine(2));
        assertTrue(input4.isStartOfLine(5));
        assertTrue(input4.isStartOfLine(10));
        assertTrue(input4.isStartOfLine(15));
        assertTrue(input4.isStartOfLine(24));
        assertTrue(input4.isStartOfLine(31));
    }

    @Test
    public void testIsEndOfLine() {
        assertTrue(input4.isEndOfLine(1));
        assertTrue(input4.isEndOfLine(4));
        assertTrue(input4.isEndOfLine(9));
        assertTrue(input4.isEndOfLine(14));
        assertTrue(input4.isEndOfLine(23));
        assertTrue(input4.isEndOfLine(30));
        assertTrue(input4.isEndOfLine(34));
    }

	@Test
	public void testMatch1() {
		assertTrue(input5.match(0, "We"));
		assertTrue(input5.match(33, "wall"));
		assertFalse(input5.match(35, "wall"));
	}
	
	@Test
	public void testMatch2() {
		assertTrue(input5.match(0, 2, "We"));
		assertTrue(input5.match(3, 11, "are just"));
	}
	
	@Test
	public void testMatch3() {
		assertTrue(input5.matchBackward(2, "We"));
		assertTrue(input5.matchBackward(37, "wall"));
		assertTrue(input5.matchBackward(37, "all"));
		assertTrue(input5.matchBackward(32, "the"));
	}

	@Test
	public void testCharAt() {
        assertEquals(Character.toCodePoint('\ud83c', '\udf55'), input6.charAt(0)); // 🍕
        assertEquals('\n', input6.charAt(1));
        assertEquals('\n', input6.charAt(2));
        assertEquals(Character.toCodePoint('\uD83E', '\uDD66'), input6.charAt(3)); // 🥦
        assertEquals(Character.toCodePoint('\uD83E', '\uDD81'), input6.charAt(4)); // 🦁
        assertEquals('a', input6.charAt(5));
        assertEquals(Character.toCodePoint('\uD83C', '\uDF46'), input6.charAt(6)); // 🍆
        assertEquals('\n', input6.charAt(7));
        assertEquals(Character.toCodePoint('\uD83D', '\uDC30'), input6.charAt(8)); // 🐰
        assertEquals(-1, input6.charAt(9));
    }

    @Test
    public void getLineCountTest() {
        assertEquals(1, input1.getLineCount());
        assertEquals(2, input2.getLineCount());
        assertEquals(2, input3.getLineCount());
        assertEquals(7, input4.getLineCount());
        assertEquals(1, input5.getLineCount());
        assertEquals(4, input6.getLineCount());
    }

    @Test
    public void isEndOfFile() {
        assertTrue(input1.isEndOfFile(0));
        assertTrue(input2.isEndOfFile(12));
        assertTrue(input3.isEndOfFile(13));
        assertTrue(input4.isEndOfFile(34));
        assertTrue(input5.isEndOfFile(37));
        assertTrue(input6.isEndOfFile(9));
    }

}
