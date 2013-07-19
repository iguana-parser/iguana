package org.jgll.util.hashing;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.Set;

import org.jgll.parser.HashFunctions;
import org.jgll.util.RandomUtil;
import org.junit.Test;

public class CuckoosHashSetTest {
	
	@Test
	public void testIn() {
		Set<IntegerHashKey4> set = new CuckooHashSet<>(16);
		IntegerHashKey4 key1 = new IntegerHashKey4(100, 12, 27, 23);
		IntegerHashKey4 key2 = new IntegerHashKey4(52, 10, 20, 21);

		boolean add1 = set.add(key1);
		boolean add2 = set.add(key2);
		assertEquals(true, add1);
		assertEquals(true, add2);
		
		
		assertEquals(true, set.contains(key1));
		assertEquals(true, set.contains(key2));
	}
	
	@Test
	public void testRehashing() {
		Set<IntegerHashKey4> set = new CuckooHashSet<>(8);
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
		
		assertEquals(1, ((CuckooHashSet<IntegerHashKey4>) set).getEnlargeCount());
	}
	
	@Test
	public void testInsertOneMillionEntries() {
		Set<IntegerHashKey4> set = new CuckooHashSet<>();
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
	
	@Test
	public void test() {
		IntegerKey key1 = new IntegerKey(1);
		IntegerKey key2 = new IntegerKey(2);
		IntegerKey key3 = new IntegerKey(3);
		
		CuckooHashSet<IntegerKey> set = new CuckooHashSet<>();
		set.add(key1);
		set.add(key2);
		set.add(key3);
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
		public int hashCode() {
			return HashFunctions.defaulFunction().hash(k1, k2, k3, k4);
		}
		
		@Override
		public String toString() {
			return "(" + k1 + ", " + k2 + ", " + k3 + ", " + k4 + ")";
		}
	}
	
	private class IntegerKey {
		
		private int k;
		
		public IntegerKey(int k) {
			this.k = k;
		}
		
		@Override
		public int hashCode() {
			return 10;
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
	}
}
