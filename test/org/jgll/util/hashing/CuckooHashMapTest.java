package org.jgll.util.hashing;

import java.util.Random;

import static org.junit.Assert.*;

import org.jgll.util.RandomUtil;
import org.junit.Test;

public class CuckooHashMapTest {
	
	@Test
	public void test() {
		CuckooHashMap<IntegerKey, Integer> map = new CuckooHashMap<>();
		map.put(IntegerKey.from(1), 2);
		map.put(IntegerKey.from(2), 3);
		
		assertEquals(new Integer(2), map.get(IntegerKey.from(1)));
		assertEquals(new Integer(3), map.get(IntegerKey.from(2)));
	}
	
	@Test
	public void testInsertOneMillionEntries() {
		CuckooHashMap<IntegerKey, Integer> map = new CuckooHashMap<>();
		Random rand = RandomUtil.random;
		for(int i = 0; i < 1000000; i++) {
			int key = rand.nextInt(Integer.MAX_VALUE);
			int value = rand.nextInt(Integer.MAX_VALUE);
			while(map.get(IntegerKey.from(key)) != null) {
				key = rand.nextInt(Integer.MAX_VALUE);
				value = rand.nextInt(Integer.MAX_VALUE);
			}
			map.put(IntegerKey.from(key), value);
		}
		
		assertEquals(1000000, map.size());
	}
	
	
	private static class IntegerKey implements HashKey {
		
		private int k;
		
		public IntegerKey(int k) {
			this.k = k;
		}
		
		public static IntegerKey from(int k) {
			return new IntegerKey(k);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			
			if(!(obj instanceof IntegerKey)) {
				return false;
			}
			
			IntegerKey other = (IntegerKey) obj;
			
			return k == other.k;
		}

		@Override
		public int hash(HashFunction f) {
			return f.hash(k);
		}
		
		@Override
		public String toString() {
			return k + "";
		}

	}



}
