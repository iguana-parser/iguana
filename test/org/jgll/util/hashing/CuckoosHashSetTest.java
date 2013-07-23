package org.jgll.util.hashing;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.jgll.util.RandomUtil;
import org.junit.Test;

public class CuckoosHashSetTest {
	
	private final IntegerHashKeyDecomposer decomposer = new IntegerHashKeyDecomposer();
	
	@Test
	public void testAdd() {
		CuckooHashSet<IntegerHashKey4> set = new CuckooHashSet<>(decomposer);
		IntegerHashKey4 key1 = new IntegerHashKey4(100, 12, 27, 23);
		IntegerHashKey4 key2 = new IntegerHashKey4(52, 10, 20, 21);

		IntegerHashKey4 add1 = set.add(key1);
		IntegerHashKey4 add2 = set.add(key2);
		assertEquals(null, add1);
		assertEquals(null, add2);
		
		assertEquals(true, set.contains(key1));
		assertEquals(true, set.contains(key2));
		
		assertEquals(2, set.size());
	}
	
	@Test
	public void testRehashing() {
		CuckooHashSet<IntegerHashKey4> set = new CuckooHashSet<>(8, decomposer);
		IntegerHashKey4 key1 = new IntegerHashKey4(100, 12, 27, 23);
		IntegerHashKey4 key2 = new IntegerHashKey4(52, 10, 20, 21);
		IntegerHashKey4 key3 = new IntegerHashKey4(10, 10, 98, 13);
		IntegerHashKey4 key4 = new IntegerHashKey4(59, 7, 98, 1);

		set.add(key1);
		set.add(key2);
		set.add(key3);
		set.add(key4);
		
		assertEquals(true, set.contains(key1));
		assertEquals(true, set.contains(key2));
		assertEquals(true, set.contains(key3));
		assertEquals(true, set.contains(key4));
		
		assertEquals(1, set.getEnlargeCount());
		assertEquals(4, set.size());
	}
	
	@Test
	public void testInsertOneMillionEntries() {
		CuckooHashSet<IntegerHashKey4> set = new CuckooHashSet<>(decomposer);
		Random rand = RandomUtil.random;
		for(int i = 0; i < 1000000; i++) {
			IntegerHashKey4 key = new IntegerHashKey4(rand.nextInt(Integer.MAX_VALUE), 
													  rand.nextInt(Integer.MAX_VALUE), 
													  rand.nextInt(Integer.MAX_VALUE), 
													  rand.nextInt(Integer.MAX_VALUE));
			set.add(key);
		}
		
		assertEquals(1000000, set.size());
	}
	
	@Test
	public void testAddAll() {
		CuckooHashSet<Integer> set1 = CuckooHashSet.from(IntegerDecomposer.getInstance(), 1, 2, 3);
		CuckooHashSet<Integer> set2 = CuckooHashSet.from(IntegerDecomposer.getInstance(), 4, 5, 6, 7);
		assertEquals(3, set1.size());
		assertEquals(4, set2.size());
		
		set1.addAll(set2);
		assertEquals(7, set1.size());
	}
	
	@Test
	public void testClear() {
		CuckooHashSet<Integer> set = CuckooHashSet.from(IntegerDecomposer.getInstance(), 1, 2, 3, 4, 5);
		set.clear();
		
		assertEquals(false, set.contains(1));
		assertEquals(false, set.contains(2));
		assertEquals(false, set.contains(3));
		assertEquals(false, set.contains(4));
		assertEquals(false, set.contains(5));
		assertEquals(0, set.size());
	}
	
	@Test
	public void testRemove() {
		CuckooHashSet<Integer> set = CuckooHashSet.from(IntegerDecomposer.getInstance(), 1, 2, 3, 4, 5);
		set.remove(3);
		set.remove(5);
		
		assertEquals(true, set.contains(1));
		assertEquals(true, set.contains(2));
		assertEquals(false, set.contains(3));
		assertEquals(true, set.contains(4));
		assertEquals(false, set.contains(5));
		assertEquals(3, set.size());
	}
	
	@Test
	public void testAddAndGet() {
		CuckooHashSet<Integer> set = CuckooHashSet.from(IntegerDecomposer.getInstance(), 1, 2, 3);
		Integer ret1 = set.add(4);
		assertEquals(null, ret1);
		
		assertEquals(true, set.contains(4));
		
		Integer ret2 = set.add(3);
		assertEquals(new Integer(3), ret2);
	}
	
	private class IntegerHashKey4 {

		private int k1;
		private int k2;
		private int k3;
		private int k4;

		public IntegerHashKey4(int k1, int k2, int k3, int k4) {
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
			
			if(!(obj instanceof IntegerHashKey4)) {
				return false;
			}
			
			IntegerHashKey4 other = (IntegerHashKey4) obj;
			
			return k1 == other.k1 && k2 == other.k2 && k3 == other.k3 && k4 == other.k4;
		}
		
		@Override
		public String toString() {
			return "(" + k1 + ", " + k2 + ", " + k3 + ", " + k4 + ")";
		}
	}
	
	private static class IntegerHashKeyDecomposer implements Decomposer<IntegerHashKey4> {

		private int[] components = new int[4];
		
		@Override
		public int[] toIntArray(IntegerHashKey4 key) {
			components[0] = key.k1;
			components[1] = key.k2;
			components[2] = key.k3;
			components[3] = key.k4;
			return components;
		}
		
	}
	
}
