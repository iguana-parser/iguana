package org.jgll.util.hashing;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

public class CuckoosHashTableTest {
	
	@Test
	public void test1() {
		CuckooHashSet set = new CuckooHashSet(16);
		IntegerHashKey key1 = new IntegerHashKey(100, 12, 27, 23);
		IntegerHashKey key2 = new IntegerHashKey(52, 10, 20, 21);

		set.add(key1);
		set.add(key2);
		
		assertEquals(true, set.contains(key1));
		assertEquals(true, set.contains(key2));
	}
	
	@Test
	public void testRehashing() {
		CuckooHashSet set = new CuckooHashSet(8);
		IntegerHashKey key1 = new IntegerHashKey(100, 12, 27, 23);
		IntegerHashKey key2 = new IntegerHashKey(52, 10, 20, 21);
		IntegerHashKey key3 = new IntegerHashKey(10, 10, 98, 13);
		IntegerHashKey key4 = new IntegerHashKey(59, 7, 98, 1);

		set.add(key1);
		set.add(key2);
		set.add(key3);
		set.add(key4);
		
		assertEquals(true, set.contains(key1));
		assertEquals(true, set.contains(key2));
		assertEquals(true, set.contains(key3));
		assertEquals(true, set.contains(key4));
		
		assertEquals(1, set.getGrowCount());
	}
	
	@Test
	public void testInsertOneMillionEntries() {
		CuckooHashSet set = new CuckooHashSet();
		Random rand = new Random();
		for(int i = 0; i < 1000000; i++) {
			IntegerHashKey key = new IntegerHashKey(rand.nextInt(Integer.MAX_VALUE), 
													rand.nextInt(Integer.MAX_VALUE), 
													rand.nextInt(Integer.MAX_VALUE), 
													rand.nextInt(Integer.MAX_VALUE));
			set.add(key);
		}
		
		System.out.println(set.getRehashCount());
		System.out.println(set.getGrowCount());
		assertEquals(1000000, set.size());
	}
	
	private class IntegerHashKey implements HashKey {

		private int k1;
		private int k2;
		private int k3;
		private int k4;

		public IntegerHashKey(int k1, int k2, int k3, int k4) {
			this.k1 = k1;
			this.k2 = k2;
			this.k3 = k3;
			this.k4 = k4;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			
			if(!(obj instanceof IntegerHashKey)) {
				return false;
			}
			
			IntegerHashKey other = (IntegerHashKey) obj;
			
			return k1 == other.k1 && k2 == other.k2 && k3 == other.k3 && k4 == other.k4;
		}
		
		@Override
		public String toString() {
			return "(" + k1 + ", " + k2 + ", " + k3 + ", " + k4 + ")";
		}

		@Override
		public int hash(HashFunction function) {
			return function.hash(k1, k2, k3, k4);
		}
		
	}

}
