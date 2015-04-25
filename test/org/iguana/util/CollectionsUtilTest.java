/*
 * Copyright (c) 2015, CWI
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

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.List;

import org.iguana.util.CollectionsUtil;
import org.junit.Test;

public class CollectionsUtilTest {

	@Test
	public void test1() throws Exception {
		List<Integer> l = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
		List<List<Integer>> expected = asList(asList(1, 2, 3), asList(4, 5, 6), asList(7, 8, 9), asList(10, 11, 12));
		assertEquals(expected, CollectionsUtil.split(l, 3));
	}
	
	@Test
	public void test2() throws Exception {
		List<Integer> l = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
		List<List<Integer>> expected = asList(asList(1, 2, 3, 4), asList(5, 6, 7, 8), asList(9, 10, 11, 12));
		assertEquals(expected, CollectionsUtil.split(l, 4));
	}
	
	@Test
	public void test3() throws Exception {
		List<Integer> l = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
		List<List<Integer>> expected = asList(asList(1, 2, 3, 4, 5), asList(6, 7, 8, 9, 10), asList(11, 12));
		assertEquals(expected, CollectionsUtil.split(l, 5));
	}
	
}
