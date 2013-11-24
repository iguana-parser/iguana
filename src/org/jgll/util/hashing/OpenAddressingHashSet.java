package org.jgll.util.hashing;

import java.util.Iterator;


public class OpenAddressingHashSet<T> implements MultiHashSet<T> {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 32;
	private static final float DEFAULT_LOAD_FACTOR = 0.5f;
	
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

		initialCapacity = Math.max(4, initialCapacity);
		initialCapacity /= loadFactor;
		
		capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
            p++;
        }
        
		bitMask = capacity - 1;
		
		threshold = (int) (loadFactor * capacity);
		table = new Object[capacity];
	}
	
	public boolean contains(T key) {
		
		int i = indexFor(key.hashCode());
		
		while(table[i] != null && !table[i].equals(key)) {			
			i = (i + 1) & bitMask; // mod capacity
		}
		
		return table[i] != null;
	}
	
	public T add(T key) {
				
		int i = indexFor(key.hashCode());
		do {
			if(table[i] == null) {
				table[i] = key;
				size++;
				if (size >= threshold) {
					rehash();
				}
				return null;
			}
			
			else if(table[i].equals(key)) {
				return key;
			}
			
			i = (i + 1) & bitMask; // mod capacity
		} while(true);
		
	}
	
	
	private void rehash() {
		throw new IllegalStateException();
//		capacity <<= 1;
//		p += 1;
//		bitMask = capacity - 1;
//		
//		Object[] newTable = new Object[capacity];
//		
//		label:
//		for(Object key : table) {
//			if(key != null) {
//				
//				int j = indexFor(key.hashCode());
//
//				do {
//					if(newTable[j] == null) {
//						newTable[j] = key;
//						continue label;
//					}
//					
//					j = next(j);
//				} while(true);
//			}
//		}
//		table = newTable;
//		
//		threshold = (int) (loadFactor * capacity);
//		rehashCount++;
	}
	
	private int indexFor(int hash) {
		return  hash & bitMask;		
	}
		
	private int next(int j) {
		if(j == 0) {
			return capacity - 1;
		}
		return j - 1;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T get(T key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int getInitialCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEnlargeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean remove(T key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addAll(Iterable<T> c) {
		// TODO Auto-generated method stub
		return false;
	}

}