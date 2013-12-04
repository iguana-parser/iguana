package org.jgll.util.hashing;

import static org.junit.Assert.*;

import java.util.Random;

import org.jgll.util.RandomUtil;
import org.junit.Test;

public class CuckooHashMapTest {
	
	@Test
	public void test() {
		CuckooHashMap<Integer, Integer> map = new CuckooHashMap<>(IntegerExternalHasher.getInstance());
		map.put(1, 2);
		map.put(2, 3);
		
		assertEquals(new Integer(2), map.get(1));
		assertEquals(new Integer(3), map.get(2));
	}
	
	@Test
	public void testInsertOneMillionEntries() {
		CuckooHashMap<Integer, Integer> map = new CuckooHashMap<>(IntegerExternalHasher.getInstance());
		Random rand = RandomUtil.random;
		for(int i = 0; i < 1000000; i++) {
			int key = rand.nextInt(Integer.MAX_VALUE);
			int value = rand.nextInt(Integer.MAX_VALUE);
			while(map.get(key) != null) {
				key = rand.nextInt(Integer.MAX_VALUE);
				value = rand.nextInt(Integer.MAX_VALUE);
			}
			map.put(key, value);
		}
		
		assertEquals(1000000, map.size());
	}


}
