package org.jgll.util.hashing;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.Set;

import org.jgll.parser.HashFunctions;
import org.junit.Test;

public class CuckoosHashSetTest {
	
	@Test
	public void testIn() {
		Set<IntegerHashKey> set = new CuckooHashSet<>(16);
		IntegerHashKey key1 = new IntegerHashKey(100, 12, 27, 23);
		IntegerHashKey key2 = new IntegerHashKey(52, 10, 20, 21);

		boolean add1 = set.add(key1);
		boolean add2 = set.add(key2);
		assertEquals(true, add1);
		assertEquals(true, add2);
		
		
		assertEquals(true, set.contains(key1));
		assertEquals(true, set.contains(key2));
	}
	
	@Test
	public void testRehashing() {
		Set<IntegerHashKey> set = new CuckooHashSet<>(8);
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
		
		assertEquals(1, ((CuckooHashSet<IntegerHashKey>) set).getEnlargeCount());
	}
	
	@Test
	public void testInsertOneMillionEntries() {
		Set<IntegerHashKey> set = new CuckooHashSet<>();
		Random rand = new Random();
		for(int i = 0; i < 1000000; i++) {
			IntegerHashKey key = new IntegerHashKey(rand.nextInt(Integer.MAX_VALUE), 
													rand.nextInt(Integer.MAX_VALUE), 
													rand.nextInt(Integer.MAX_VALUE), 
													rand.nextInt(Integer.MAX_VALUE));
			set.add(key);
		}
		
		assertEquals(1000000, set.size());
	}
	
	@Test
	public void testAddAll() {
		Set<Integer> set1 = CuckooHashSet.from(1, 2, 3);
		Set<Integer> set2 = CuckooHashSet.from(4, 5, 6, 7);
		assertEquals(3, set1.size());
		assertEquals(4, set2.size());
		
		set1.addAll(set2);
		assertEquals(7, set1.size());
	}
	
	@Test
	public void testClear() {
		Set<Integer> set = CuckooHashSet.from(1, 2, 3, 4, 5);
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
		Set<Integer> set = CuckooHashSet.from(1, 2, 3, 4, 5);
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
		CuckooHashSet<Integer> set = CuckooHashSet.from(1, 2, 3);
		Integer ret1 = set.addAndGet(4);
		assertEquals(null, ret1);
		
		assertEquals(true, set.contains(4));
		
		int ret2 = set.addAndGet(3);
		assertEquals(3, ret2);
	}
	
	
	private class IntegerHashKey {

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
		public int hashCode() {
			return HashFunctions.defaulFunction().hash(k1, k2, k3, k4);
		}
		
		@Override
		public String toString() {
			return "(" + k1 + ", " + k2 + ", " + k3 + ", " + k4 + ")";
		}

	}

}
