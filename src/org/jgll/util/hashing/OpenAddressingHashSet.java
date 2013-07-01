package org.jgll.util.hashing;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


public class OpenAddressingHashSet<E> implements Set<E> {
	
	private static final int DEFAULT_INITIAL_CAPACITY = 32;
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	private int initialCapacity;
	
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
		this.initialCapacity = initialCapacity;
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
		
		int i = indexFor(key.hashCode());
		
		while(table[i] != null && !table[i].equals(key)) {			
			i = (i + 1) & bitMask; // mod capacity
		}
		
		return table[i] != null;
	}
	
	@Override
	public boolean add(Object key) {
				
		int i = indexFor(key.hashCode());
		do {
			if(table[i] == null) {
				table[i] = key;
				size++;
				if (size >= threshold) {
					rehash();
				}
				return true;
			}
			
			else if(table[i].equals(key)) {
				return false;
			}
			
			i = (i + 1) & bitMask; // mod capacity
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
				
				int i = indexFor(key.hashCode());

				do {
					if(newTable[i] == null) {
						newTable[i] = key;
						continue label;
					}
					
					i = (i + 1) & bitMask;
				} while(true);
			}
		}
		table = newTable;
		
		threshold = (int) (loadFactor * capacity);
		rehashCount++;
	}
	
	private int indexFor(int hash) {
		return  hash & bitMask;		
	}
		
	public int getRehashCount() {
		return rehashCount;
	}
	
	public int getInitialCapacity() {
		return initialCapacity;
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
		throw new UnsupportedOperationException();
	}

}
