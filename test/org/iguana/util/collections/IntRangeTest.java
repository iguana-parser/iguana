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

package org.iguana.util.collections;

import static org.junit.Assert.*;

import org.iguana.util.collections.IntRangeTree;
import org.junit.Test;

public class IntRangeTest {
	
	@Test
	public void test() {
		IntRangeTree tree = new IntRangeTree();
		tree.insert('.', 1);
		tree.insert('$', 2);
		tree.insert('&', 3);
		tree.insert('*', 4);
		tree.insert(':', 5);
		tree.insert('<', 6);
		tree.insert('>', 7);
		tree.insert('@', 8);
		tree.insert('\\', 9);
		tree.insert('^', 10);
		tree.insert('|', 11);
		tree.insert('~', 12);
		tree.insert('!', 13);
		tree.insert('#', 14);
		tree.insert('%', 15);
		tree.insert('+', 16);
		tree.insert('-', 17);
		tree.insert('/', 18);
		tree.insert('=', 19);
		tree.insert('?', 20);
		assertEquals(20, tree.size());
		assertEquals(4, tree.getRoot().getHeight());
	}
	
	@Test
	public void test5() {
		IntRangeTree tree = new IntRangeTree();
		tree.insert(44, 1);
		tree.insert(17, 2);
		tree.insert(32, 3);
		tree.insert(78, 4);
		tree.insert(50, 5);
		tree.insert(48, 6);
		tree.insert(62, 7);
		tree.insert(54, 8);
		tree.insert(88, 9);
		assertEquals(9, tree.size());
		assertEquals(3, tree.getRoot().getHeight());
	}
	
	@Test
	public void test6() {
		IntRangeTree tree = new IntRangeTree();
		tree.insert(14, 1);
		tree.insert(17, 2);
		tree.insert(11, 3);
		tree.insert(7,  4);
		tree.insert(53, 5);
		tree.insert(4,  6);
		tree.insert(13, 7);
		assertEquals(7, tree.size());
		assertEquals(3, tree.getRoot().getHeight());
	}
	
	@Test
	public void test7() {
		IntRangeTree tree = new IntRangeTree();
		tree.insert(3, 1);
		tree.insert(2, 2);
		tree.insert(1, 3);
		tree.insert(4, 4);
		tree.insert(5, 5);
		tree.insert(6, 6);
		tree.insert(7, 7);
		tree.insert(16, 8);
		tree.insert(15, 9);
		tree.insert(14, 10);
		assertEquals(10, tree.size());
		assertEquals(3, tree.getRoot().getHeight());
	}

}
