package org.jgll.util.hashing;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * 
 * A hash set based on the chuckoo's hashing algorithm.
 * 
 * @author Ali Afroozeh
 *
 */
public class CuckooHashSet<E> implements Set<E>{
	
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.49f;
	
	private int initialCapacity;
	
	private int capacity;
	
	private int p;
	
	private int size;
	
	private int threshold;
	
	private float loadFactor;
	
	private int rehashCount;
	
	private int growCount;
	
	private int a1, a2, b1, b2;
		
	private Object[] table1;

	private Object[] table2;
	
	private static int maxA = Integer.MAX_VALUE >> 1;

	private int maxB;
	
	
	public CuckooHashSet() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public CuckooHashSet(int initalCapacity) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	public CuckooHashSet(int initialCapacity, float loadFactor) {
		this.initialCapacity = initialCapacity;
		
		if(initialCapacity < 8) {
			initialCapacity = 8;
		}
		
		this.loadFactor = loadFactor;

		capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
            p++;
        }
        
		threshold = (int) (loadFactor * capacity);

        // For each table
		p--;
		
		int tableSize = capacity >> 1;
		table1 = new Object[tableSize];
		table2 = new Object[tableSize];
		
		maxB = 1 << (32 - p);
		
		generateNewHashFunctions();
	}

	private void generateNewHashFunctions() {
		Random rand = new Random();
		a1 = 2 * rand.nextInt(maxA) + 1;
		b1 = rand.nextInt(maxB);
		a2 = 2 * rand.nextInt(maxA) + 1;
		b2 = rand.nextInt(maxB);
	}
	
	@Override
	public boolean contains(Object key) {
		return contains(key, table1, table2);
	}
	
	public boolean contains(Object key, Object[] table1, Object[] table2) {
		int index = hash1(key);
		if(index > table1.length) {
			System.out.println("WTF?");
		}
		if(key.equals(table1[index])) {
			return true;
		}
		
		index = hash2(key);
		if(key.equals(table2[index])) {
			return true;
		}
		
		return false;
	}
	
	private boolean insertAgain(Object key, Object[] table1, Object[] table2) {
		int i = 0;
		while(i < size + 1) {
			i++;

			int index = hash1(key);
			if(table1[index] == null) {
				table1[index] = key;
				return true;
			}
			
			Object tmp = table1[index];
			table1[index] = key;
			key = tmp;
			
			index = hash2(key);
			if(table2[index] == null) {
				table2[index] = key;
				return true;
			}
			
			tmp = table2[index];
			table2[index] = key;
			key = tmp;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean add(E key) {
		
		if(contains(key)) {
			return false;
		}
		
		int i = 0;
		while(i < size + 1) {
			i++;
			
			int index = hash1(key);
			if(table1[index] == null) {
				table1[index] = key;
				size++;
				if(size >= threshold) {
					enlargeTables();
				}
				return true;
			}
			
			E tmp = (E) table1[index];
			table1[index] = key;
			key = tmp;
			
			index = hash2(key);
			if(table2[index] == null) {
				table2[index] = key;
				size++;
				if(size >= threshold) {
					enlargeTables();
				}
				return true;
			}
			
			tmp = (E) table2[index];
			table2[index] = key;
			key = tmp;
		}
		
		rehash();
		return add(key);
	}
	
	private void rehash() {
		Object[] newTable1 = new Object[table1.length];
		Object[] newTable2 = new Object[table2.length];

		generateNewHashFunctions();
		
		for(Object key : table1) {
			if(key != null) {
				// if one element cannot be inserted, restart the whole process with two new
				// hash function.
				if(!insertAgain(key, newTable1, newTable2)) {
					rehash();
					return;
				}
			}
		}
		
		for(Object key : table2) {
			if(key != null) {
				// if one element cannot be inserted, restart the whole process with two new
				// hash function.
				if(!insertAgain(key, newTable1, newTable2)) {
					rehash();
					return;
				}
			}
		}
		
		table1 = newTable1;
		table2 = newTable2;
		
		rehashCount++;
	}
	
	private void enlargeTables() {
		
		Object[] newTable1 = new Object[capacity];
		Object[] newTable2 = new Object[capacity];
		
		capacity <<= 1;
		p++;
		
		threshold = (int) (loadFactor * capacity);
		
		int i = 0;
		for(Object key : table1) {
			if(key != null) {
				newTable1[i++] = key;
			}
		}
		
		for(Object key : table2) {
			if(key != null) {
				newTable1[i++] = key;
			}
		}
		
		table1 = newTable1;
		table2 = newTable2;
		
		growCount++;
		rehash();
	}
	
	public int size() {
		return size;
	}
	
	public int getInitialCapacity() {
		return initialCapacity;
	}
	
	public int getRehashCount() {
		return rehashCount;
	}
	
	public int getGrowCount() {
		return growCount;
	}
	
	private int hash1(Object key) {
		return (a1 * key.hashCode() + b1) >>> (32 - p);
	}
	
	private int hash2(Object key) {
		return (a2 * key.hashCode() + b2) >>> (32 - p);
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}


}
