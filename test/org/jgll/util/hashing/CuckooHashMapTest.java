package org.jgll.util.hashing;

import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

import org.junit.Test;

public class CuckooHashMapTest {
	
	@Test
	public void test() {
		Map<Integer, Integer> map = new CuckooHashMap<>();
		map.put(1, 2);
		map.put(2, 3);
		
		assertEquals(2, (int)map.get(1));
		assertEquals(3, (int)map.get(2));
	}
	
	@Test
	public void testInsertOneMillionEntries() {
		Map<Integer, Integer> map = new CuckooHashMap<>();
		Random rand = new Random();
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
