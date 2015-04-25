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
