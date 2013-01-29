package org.jgll.util;

import java.util.Set;

import org.junit.Test;

public class HashSetTest {
	
	@Test
	public void test() {
		Set<Integer> set = new OpenAddressingHashSet<>(8);
		set.add(1);
		set.add(2);
		set.add(2);
		set.add(4);
		set.add(5);
		set.add(6);
		set.add(7);
		set.add(8);
		set.add(9);
		set.add(10);
		set.add(11);
		set.add(12);
	}
	
	@Test
	public void test2() {
		long A = 2654435769L;
		long r = A * 123456;
		int y = (int) r;
		int x = (y >> (32 - 14)) & 16383;
		System.out.println(x);
	}

}
