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

package org.iguana.utils.collections;

import org.iguana.utils.collections.rangemap.IntRangeMap;
import org.iguana.utils.collections.rangemap.Range;
import org.iguana.utils.collections.rangemap.RangeMapBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntRangeTest {

	@Test
	public void test() {
		RangeMapBuilder<Integer> builder = new RangeMapBuilder<>();
        builder.put(Range.in('.', '.'), 1);
		builder.put(Range.in('$', '$'), 2);
		builder.put(Range.in('&', '&'), 3);
		builder.put(Range.in('*', '*'), 4);
		builder.put(Range.in(':', ':'), 5);
		builder.put(Range.in('<', '<'), 6);
		builder.put(Range.in('>', '>'), 7);
		builder.put(Range.in('@', '@'), 8);
		builder.put(Range.in('\\', '\\'), 9);
		builder.put(Range.in('^', '^'), 10);
		builder.put(Range.in('|', '|'), 11);
		builder.put(Range.in('~', '~'), 12);
		builder.put(Range.in('!', '!'), 13);
		builder.put(Range.in('#', '#'), 14);
		builder.put(Range.in('%', '%'), 15);
		builder.put(Range.in('+', '+'), 16);
		builder.put(Range.in('-', '-'), 17);
		builder.put(Range.in('/', '/'), 18);
		builder.put(Range.in('=', '='), 19);
		builder.put(Range.in('?', '?'), 20);

		IntRangeMap map = builder.buildIntRangeMap();
		
		assertEquals(1, map.get('.'));
		assertEquals(2, map.get('$'));
		assertEquals(3, map.get('&'));
		assertEquals(4, map.get('*'));
		assertEquals(5, map.get(':'));
		assertEquals(6, map.get('<'));
		assertEquals(7, map.get('>'));
		assertEquals(8, map.get('@'));
		assertEquals(9, map.get('\\'));
		assertEquals(10, map.get('^'));
		assertEquals(11, map.get('|'));
		assertEquals(12, map.get('~'));
		assertEquals(13, map.get('!'));
		assertEquals(14, map.get('#'));
		assertEquals(15, map.get('%'));
		assertEquals(16, map.get('+'));
		assertEquals(17, map.get('-'));
		assertEquals(18, map.get('/'));
		assertEquals(19, map.get('='));
		assertEquals(20, map.get('?'));
	}
	
	@Test
	public void test5() {
		RangeMapBuilder<Integer> builder = new RangeMapBuilder<>();
		builder.put(Range.in(44, 44), 1);
		builder.put(Range.in(17, 17), 2);
		builder.put(Range.in(32, 32), 3);
		builder.put(Range.in(78, 78), 4);
		builder.put(Range.in(50, 50), 5);
		builder.put(Range.in(48, 48), 6);
		builder.put(Range.in(62, 62), 7);
		builder.put(Range.in(54, 54), 8);
		builder.put(Range.in(88, 88), 9);

		IntRangeMap map = builder.buildIntRangeMap();
		
		assertEquals(1, map.get(44));
		assertEquals(2, map.get(17));
		assertEquals(3, map.get(32));
		assertEquals(4, map.get(78));
		assertEquals(5, map.get(50));
		assertEquals(6, map.get(48));
		assertEquals(7, map.get(62));
		assertEquals(8, map.get(54));
		assertEquals(9, map.get(88));
	}
	
	@Test
	public void test6() {
		RangeMapBuilder<Integer> builder = new RangeMapBuilder<>();
		builder.put(Range.in(14, 14), 1);
		builder.put(Range.in(17, 17), 2);
		builder.put(Range.in(11, 11), 3);
		builder.put(Range.in(7,  7), 4);
		builder.put(Range.in(53, 53), 5);
		builder.put(Range.in(4,  4), 6);
		builder.put(Range.in(13, 13), 7);

		IntRangeMap map = builder.buildIntRangeMap();

		assertEquals(1, map.get(14));
		assertEquals(2, map.get(17));
		assertEquals(3, map.get(11));
		assertEquals(4, map.get(7));
		assertEquals(5, map.get(53));
		assertEquals(6, map.get(4));
		assertEquals(7, map.get(13));
	}
	
	@Test
	public void test7() {
		RangeMapBuilder<Integer> builder = new RangeMapBuilder<>();
		builder.put(Range.in(3, 3), 1);
		builder.put(Range.in(2, 2), 2);
		builder.put(Range.in(1, 1), 3);
		builder.put(Range.in(4, 4), 4);
		builder.put(Range.in(5, 5), 5);
		builder.put(Range.in(6, 6), 6);
		builder.put(Range.in(7, 7), 7);
		builder.put(Range.in(16, 16), 8);
		builder.put(Range.in(15, 15), 9);
		builder.put(Range.in(14, 14), 10);

		IntRangeMap map = builder.buildIntRangeMap();
		
		assertEquals(1, map.get(3));
		assertEquals(2, map.get(2));
		assertEquals(3, map.get(1));
		assertEquals(4, map.get(4));
		assertEquals(5, map.get(5));
		assertEquals(6, map.get(6));
		assertEquals(7, map.get(7));
		assertEquals(8, map.get(16));
		assertEquals(9, map.get(15));
		assertEquals(10, map.get(14));
	}
	
	@Test
	public void test8() {
		RangeMapBuilder<Integer> builder = new RangeMapBuilder<>();

		// [*, \u0000, \, \\u000A, \\u000D, \uFFFFFFFF, ]-\u10FFFF, \u0001-[]
        builder.put(Range.in('*', '*'), 1);
		builder.put(Range.in('\u0000', '\u0000'), 1);
		builder.put(Range.in('\\', '\\'), 1);
		builder.put(Range.in('\r', '\r'), 1);
		builder.put(Range.in('\n', '\n'), 1);
		builder.put(Range.in(']', 1_114_111), 1);
		builder.put(Range.in(1, '['), 1);

		IntRangeMap map = builder.buildIntRangeMap();

		assertEquals(1, map.get(' '));
	}
	
	@Test
	public void test9() {
		RangeMapBuilder<Integer> builder = new RangeMapBuilder<>();
		builder.put(Range.in('A', 'Z'), 1);
		builder.put(Range.in('d', 'd'), 1);
		builder.put(Range.in(',', ','), 1);
		builder.put(Range.in('0', '0'), 1);
		builder.put(Range.in('\\', '\\'), 1);
		builder.put(Range.in('f', 'f'), 1);
		builder.put(Range.in('l', 'l'), 1);
		builder.put(Range.in('~', '~'), 1);
		builder.put(Range.in('p', 'p'), 1);
		builder.put(Range.in('\u0020', '\u0020'), 1);
		builder.put(Range.in('"', '"'), 1);
		builder.put(Range.in('$', '$'), 1);
		builder.put(Range.in('(', '('), 1);
		builder.put(Range.in('j','k'), 1);
		builder.put(Range.in('c', 'c'), 1);
		builder.put(Range.in('i', 'i'), 1);
		builder.put(Range.in('e', 'e'), 1);
		builder.put(Range.in('{', '{'), 1);
		builder.put(Range.in('#', '#'), 1);
		builder.put(Range.in(-1, -1), 1);
		builder.put(Range.in(13, 13), 1);
		builder.put(Range.in('!', '!'), 1);
		builder.put(Range.in('\'', '\''), 1);
		builder.put(Range.in(')', ')'), 1);
		builder.put(Range.in('-', '-'), 1);
		builder.put(Range.in('[', '['), 1);
		builder.put(Range.in('_', '_'), 1);
		builder.put(Range.in('a', 'b'), 1);
		builder.put(Range.in('g', 'h'), 1);
		builder.put(Range.in('m', 'o'), 1);
		builder.put(Range.in('\u0009', '\u000B'), 1);
		builder.put(Range.in('1', '9'), 1);
		builder.put(Range.in('q', 'z'), 1);

		IntRangeMap map = builder.buildIntRangeMap();
		
		assertEquals(1, map.get('A'));
		assertEquals(1, map.get('D'));
		assertEquals(1, map.get('Z'));
		assertEquals(1, map.get('d'));
		assertEquals(1, map.get('x'));
	}

}
