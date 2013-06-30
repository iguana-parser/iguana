package org.jgll.util.hashing;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


public class OpenAddressingHashSet<E> implements Set<E> {
	
	/**
	 * The constant suggested by Knuth for multiplicative hashing
	 */
	private static final long a = 2654435769L;

	private static final int DEFAULT_INITIAL_CAPACITY = 32;
	private static final float DEFAULT_LOAD_FACTOR = 0.7f;
	
	private int capacity;
	
	private int size;
	
	private int threshold;
	
	private float loadFactor;
	
	private int rehashCount;

	/**
	 * capacity - 1
	 * The bitMask is used to get the p most-significant bytes of
	 * the multiplicaiton.
	 */
	private int bitMask;
	
	/**
	 * capacity = 2 ^ p
	 */
	private int p;
	
	private Object[] table;
	
	public OpenAddressingHashSet() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public OpenAddressingHashSet(int initalCapacity) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	public OpenAddressingHashSet(int initialCapacity, float loadFactor) {
		this.loadFactor = loadFactor;

		capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
            p++;
        }
		
		bitMask = capacity - 1;
		
		threshold = (int) (loadFactor * capacity);
		table = new Object[capacity];
	}
	
	@Override
	public boolean contains(Object key) {
		
		int j = indexFor(key.hashCode());
		
		do {
			if(table[j] == null) {
				return false;
			}
			
			if(table[j].equals(key)) {
				return true;
			}
			
			j = next(j);
			
		} while(true);
	}
	
	@Override
	public boolean add(Object key) {
				
		int j = indexFor(key.hashCode());
		do {
			if(table[j] == null) {
				table[j] = key;
				size++;
				if (size >= threshold) {
					rehash();
				}
				return true;
			}
			
			else if(table[j].equals(key )) {
				return false;
			}
			
			j = next(j);
		} while(true);
		
	}
	
	
	private void rehash() {
		capacity <<= 1;
		p += 1;
		bitMask = capacity - 1;
		
		Object[] newTable = new Object[capacity];
		
		label:
		for(Object key : table) {
			if(key != null) {
				
				int j = indexFor(key.hashCode());

				do {
					if(newTable[j] == null) {
						newTable[j] = key;
						continue label;
					}
					
					j = next(j);
				} while(true);
			}
		}
		table = newTable;
		
		threshold = (int) (loadFactor * capacity);
		rehashCount++;
	}
	
	private int indexFor(int hash) {
		return  ((int)(a * hash) >> (32 - p)) & bitMask;		
	}
		
	private int next(int j) {
		j = j - 1;
		if(j < 0) {
			j += capacity;
		}
		return j;
	}

	public int getRehashCount() {
		return rehashCount;
	}
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
	}

}
