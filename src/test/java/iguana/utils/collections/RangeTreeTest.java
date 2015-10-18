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

import iguana.utils.collections.rangemap.RangeTree;
import org.junit.Test;

import static iguana.utils.collections.rangemap.ImmutableRange.in;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RangeTreeTest {

	@Test
	public void test1() {
		RangeTree<String> rangeTree = new RangeTree<>();
		rangeTree.insert(in(1, 4), "a");
		rangeTree.insert(in(7, 13), "b");
		rangeTree.insert(in(18, 101), "c");
		checkAsserts(rangeTree);
	}
	
	@Test
	public void test2() {
		RangeTree<String> rangeTree = new RangeTree<>();
		rangeTree.insert(in(18, 101), "c");
		rangeTree.insert(in(7, 13), "b");
		rangeTree.insert(in(1, 4), "a");
		checkAsserts(rangeTree);
	}
	
	@Test
	public void test3() {
		RangeTree<String> rangeTree = new RangeTree<>();
		rangeTree.insert(in(18, 101), "c");
		rangeTree.insert(in(1, 4), "a");
		rangeTree.insert(in(7, 13), "b");
		checkAsserts(rangeTree);
	}
	
	@Test
	public void test4() {
		RangeTree<String> rangeTree = new RangeTree<>();
		rangeTree.insert(in(7, 13), "b");
		rangeTree.insert(in(18, 101), "c");
		rangeTree.insert(in(1, 4), "a");
		checkAsserts(rangeTree);
	}
		
	private static <T> void checkAsserts(RangeTree<T> rangeTree) {
		assertEquals(null, rangeTree.get(0));
		assertEquals("a",  rangeTree.get(1));
		assertEquals("a",  rangeTree.get(3));
		assertEquals("a",  rangeTree.get(4));
		assertEquals(null, rangeTree.get(5));
		assertEquals("b",  rangeTree.get(7));
		assertEquals("b",  rangeTree.get(12));
		assertEquals("b",  rangeTree.get(13));
		assertEquals("c",  rangeTree.get(18));
		assertEquals("c",  rangeTree.get(101));
		assertEquals(null, rangeTree.get(121));

		assertTrue(rangeTree.isBalanced());
		assertEquals(3, rangeTree.size());
	}
	
}
