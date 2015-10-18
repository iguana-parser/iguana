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

package iguana.utils.collections;

import iguana.utils.collections.rangemap.*;
import org.junit.Test;

import static iguana.utils.collections.CollectionsUtil.list;
import static iguana.utils.collections.rangemap.ImmutableRange.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class IntRangeTest {
	
	@Test
	public void test() {
		IntRangeTree tree = new AVLIntRangeTree();
        tree.insert(in('.', '.'), 1);
		tree.insert(in('$', '$'), 2);
		tree.insert(in('&', '&'), 3);
		tree.insert(in('*', '*'), 4);
		tree.insert(in(':', ':'), 5);
		tree.insert(in('<', '<'), 6);
		tree.insert(in('>', '>'), 7);
		tree.insert(in('@', '@'), 8);
		tree.insert(in('\\', '\\'), 9);
		tree.insert(in('^', '^'), 10);
		tree.insert(in('|', '|'), 11);
		tree.insert(in('~', '~'), 12);
		tree.insert(in('!', '!'), 13);
		tree.insert(in('#', '#'), 14);
		tree.insert(in('%', '%'), 15);
		tree.insert(in('+', '+'), 16);
		tree.insert(in('-', '-'), 17);
		tree.insert(in('/', '/'), 18);
		tree.insert(in('=', '='), 19);
		tree.insert(in('?', '?'), 20);
		
		assertEquals(20, tree.size());
		assertEquals(4, tree.getRoot().getHeight());
		
		assertEquals(1, tree.get('.'));
		assertEquals(2, tree.get('$'));
		assertEquals(3, tree.get('&'));
		assertEquals(4, tree.get('*'));
		assertEquals(5, tree.get(':'));
		assertEquals(6, tree.get('<'));
		assertEquals(7, tree.get('>'));
		assertEquals(8, tree.get('@'));
		assertEquals(9, tree.get('\\'));
		assertEquals(10, tree.get('^'));
		assertEquals(11, tree.get('|'));
		assertEquals(12, tree.get('~'));
		assertEquals(13, tree.get('!'));
		assertEquals(14, tree.get('#'));
		assertEquals(15, tree.get('%'));
		assertEquals(16, tree.get('+'));
		assertEquals(17, tree.get('-'));
		assertEquals(18, tree.get('/'));
		assertEquals(19, tree.get('='));
		assertEquals(20, tree.get('?'));
		
		tree = new ArrayIntRangeTree(tree);
		assertEquals(1, tree.get('.'));
		assertEquals(2, tree.get('$'));
		assertEquals(3, tree.get('&'));
		assertEquals(4, tree.get('*'));
		assertEquals(5, tree.get(':'));
		assertEquals(6, tree.get('<'));
		assertEquals(7, tree.get('>'));
		assertEquals(8, tree.get('@'));
		assertEquals(9, tree.get('\\'));
		assertEquals(10, tree.get('^'));
		assertEquals(11, tree.get('|'));
		assertEquals(12, tree.get('~'));
		assertEquals(13, tree.get('!'));
		assertEquals(14, tree.get('#'));
		assertEquals(15, tree.get('%'));
		assertEquals(16, tree.get('+'));
		assertEquals(17, tree.get('-'));
		assertEquals(18, tree.get('/'));
		assertEquals(19, tree.get('='));
		assertEquals(20, tree.get('?'));
	}
	
	@Test
	public void test5() {
		IntRangeTree tree = new AVLIntRangeTree();
		tree.insert(in(44, 44), 1);
		tree.insert(in(17, 17), 2);
		tree.insert(in(32, 32), 3);
		tree.insert(in(78, 78), 4);
		tree.insert(in(50, 50), 5);
		tree.insert(in(48, 48), 6);
		tree.insert(in(62, 62), 7);
		tree.insert(in(54, 54), 8);
		tree.insert(in(88, 88), 9);
		
		assertEquals(9, tree.size());
		assertEquals(3, tree.getRoot().getHeight());
		
		assertEquals(1, tree.get(44));
		assertEquals(2, tree.get(17));
		assertEquals(3, tree.get(32));
		assertEquals(4, tree.get(78));
		assertEquals(5, tree.get(50));
		assertEquals(6, tree.get(48));
		assertEquals(7, tree.get(62));
		assertEquals(8, tree.get(54));
		assertEquals(9, tree.get(88));
		
		tree = new ArrayIntRangeTree(tree);
		
		assertEquals(1, tree.get(44));
		assertEquals(2, tree.get(17));
		assertEquals(3, tree.get(32));
		assertEquals(4, tree.get(78));
		assertEquals(5, tree.get(50));
		assertEquals(6, tree.get(48));
		assertEquals(7, tree.get(62));
		assertEquals(8, tree.get(54));
		assertEquals(9, tree.get(88));
	}
	
	@Test
	public void test6() {
		IntRangeTree tree = new AVLIntRangeTree();
		tree.insert(in(14, 14), 1);
		tree.insert(in(17, 17), 2);
		tree.insert(in(11, 11), 3);
		tree.insert(in(7,  7), 4);
		tree.insert(in(53, 53), 5);
		tree.insert(in(4,  4), 6);
		tree.insert(in(13, 13), 7);
		
		assertEquals(7, tree.size());
		assertEquals(3, tree.getRoot().getHeight());
		
		assertEquals(1, tree.get(14));
		assertEquals(2, tree.get(17));
		assertEquals(3, tree.get(11));
		assertEquals(4, tree.get(7));
		assertEquals(5, tree.get(53));
		assertEquals(6, tree.get(4));
		assertEquals(7, tree.get(13));
		
		tree = new ArrayIntRangeTree(tree);
		
		assertEquals(1, tree.get(14));
		assertEquals(2, tree.get(17));
		assertEquals(3, tree.get(11));
		assertEquals(4, tree.get(7));
		assertEquals(5, tree.get(53));
		assertEquals(6, tree.get(4));
		assertEquals(7, tree.get(13));
	}
	
	@Test
	public void test7() {
		IntRangeTree tree = new AVLIntRangeTree();
		tree.insert(in(3, 3), 1);
		tree.insert(in(2, 2), 2);
		tree.insert(in(1, 1), 3);
		tree.insert(in(4, 4), 4);
		tree.insert(in(5, 5), 5);
		tree.insert(in(6, 6), 6);
		tree.insert(in(7, 7), 7);
		tree.insert(in(16, 16), 8);
		tree.insert(in(15, 15), 9);
		tree.insert(in(14, 14), 10);
		
		assertEquals(10, tree.size());
		assertEquals(3, tree.getRoot().getHeight());
		
		assertEquals(1, tree.get(3));
		assertEquals(2, tree.get(2));
		assertEquals(3, tree.get(1));
		assertEquals(4, tree.get(4));
		assertEquals(5, tree.get(5));
		assertEquals(6, tree.get(6));
		assertEquals(7, tree.get(7));
		assertEquals(8, tree.get(16));
		assertEquals(9, tree.get(15));
		assertEquals(10, tree.get(14));
		
		tree = new ArrayIntRangeTree(tree);

		assertEquals(1, tree.get(3));
		assertEquals(2, tree.get(2));
		assertEquals(3, tree.get(1));
		assertEquals(4, tree.get(4));
		assertEquals(5, tree.get(5));
		assertEquals(6, tree.get(6));
		assertEquals(7, tree.get(7));
		assertEquals(8, tree.get(16));
		assertEquals(9, tree.get(15));
		assertEquals(10, tree.get(14));
	}
	
	@Test
	public void test8() {
		AVLIntRangeTree tree = new AVLIntRangeTree();
		// [*, \u0000, \, \\u000A, \\u000D, \uFFFFFFFF, ]-\u10FFFF, \u0001-[]
        List<Range> list = list(in('*', '*'),
				                         in('\u0000', '\u0000'), 
				                         in('\\', '\\'), 
				                         in('\r', '\r'), 
				                         in('\n', '\n'),
				                         in(']', 1_114_111),
				                         in(1, '[')
										 );
		
		Ranges.toNonOverlappingSet(list).forEach(range -> tree.insert(range, 1));

		assertEquals(1, tree.get(' '));
	}
	
	@Test
	public void test9() {
		IntRangeTree tree = new AVLIntRangeTree();
		tree.insert(in('A', 'Z'), 1);
		tree.insert(in('d', 'd'), 1);
		tree.insert(in(',', ','), 1);
		tree.insert(in('0', '0'), 1);
		tree.insert(in('\\', '\\'), 1);
		tree.insert(in('f', 'f'), 1);
		tree.insert(in('l', 'l'), 1);
		tree.insert(in('~', '~'), 1);
		tree.insert(in('p', 'p'), 1);
		tree.insert(in('\u0020', '\u0020'), 1);
		tree.insert(in('"', '"'), 1);
		tree.insert(in('$', '$'), 1);
		tree.insert(in('(', '('), 1);
		tree.insert(in('j','k'), 1);
		tree.insert(in('c', 'c'), 1);
		tree.insert(in('i', 'i'), 1);
		tree.insert(in('e', 'e'), 1);
		tree.insert(in('{', '{'), 1);
		tree.insert(in('#', '#'), 1);
		tree.insert(in(-1, -1), 1);
		tree.insert(in(13, 13), 1);
		tree.insert(in('!', '!'), 1);
		tree.insert(in('\'', '\''), 1);
		tree.insert(in(')', ')'), 1);
		tree.insert(in('-', '-'), 1);
		tree.insert(in('[', '['), 1);
		tree.insert(in('_', '_'), 1);
		tree.insert(in('a', 'b'), 1);
		tree.insert(in('g', 'h'), 1);
		tree.insert(in('m', 'o'), 1);
		tree.insert(in('\u0009', '\u000B'), 1);
		tree.insert(in('1', '9'), 1);
		tree.insert(in('q', 'z'), 1);
		
		assertEquals(1, tree.get('A'));
		assertEquals(1, tree.get('D'));
		assertEquals(1, tree.get('Z'));
		assertEquals(1, tree.get('d'));
		assertEquals(1, tree.get('x'));
		
		tree = new ArrayIntRangeTree(tree);
		
		assertEquals(1, tree.get('A'));
		assertEquals(1, tree.get('D'));
		assertEquals(1, tree.get('Z'));
		assertEquals(1, tree.get('d'));
		assertEquals(1, tree.get('x'));
	}

}
