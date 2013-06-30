package org.jgll.util.hashing;

import static org.junit.Assert.*;

import org.junit.Test;

public class OpenAddressingHashSetTest {
	
	@Test
	public void test1() {
		OpenAddressingHashSet<Integer> set = new OpenAddressingHashSet<>();
		set.add(1);
		set.add(2);
		
		assertEquals(true, set.contains(1));
		assertEquals(true, set.contains(1));
	}

}
