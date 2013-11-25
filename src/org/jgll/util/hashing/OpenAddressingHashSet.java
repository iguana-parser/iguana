package org.jgll.util.hashing;

import java.util.Iterator;

import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class OpenAddressingHashSet<T> implements MultiHashSet<T> {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 32;
	private static final float DEFAULT_LOAD_FACTOR = 0.7f;
	
	private int initialCapacity;
	
	private int capacity;
	
	private int size;
	
	private int threshold;
	
	private float loadFactor;
	
	private int rehashCount;
	
	private ExternalHasher<T> hasher;
	
	private HashFunction hashFunction = HashFunctions.defaulFunction();

	/**
	 * capacity - 1
	 * The bitMask is used to get the p most-significant bytes of the multiplicaiton.
	 */
	private int bitMask;
	
	/**
	 * capacity = 2 ^ p
	 */
	private int p;
	
	private T[] table;
	
	public OpenAddressingHashSet(ExternalHasher<T> hasher) {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, hasher);
	}
	
	public OpenAddressingHashSet(int initalCapacity, ExternalHasher<T> hasher) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR, hasher);
	}
	
	@SuppressWarnings("unchecked")
	public OpenAddressingHashSet(int initialCapacity, float loadFactor, ExternalHasher<T> hasher) {
		
		this.initialCapacity = initialCapacity;
		
		this.hasher = hasher;
		
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
		table = (T[]) new Object[capacity];
	}
	
	@Override
	public boolean contains(T key) {
		return get(key) != null;
	}
	
	@Override
	public T add(T key) {
				
		int i = indexFor(key);
		do {
			if(table[i] == null) {
				table[i] = key;
				size++;
				if (size >= threshold) {
					rehash();
				}
				return null;
			}
			
			else if(hasher.equals(table[i], key)) {
				return key;
			}
			
			i = next(i); // mod capacity
		} while(true);
	}
	
	private void rehash() {
		capacity <<= 1;
		p += 1;
		bitMask = capacity - 1;
		
		@SuppressWarnings("unchecked")
		T[] newTable = (T[]) new Object[capacity];
		
		label:
		for(T entry : table) {
			if(entry != null) {
				
				int j = indexFor(entry);
				
				do {
					if(newTable[j] == null) {
						newTable[j] = entry;
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
	
	private int indexFor(T key) {
		return hasher.hash(key, hashFunction) & bitMask;
	}
	 
	private int next(int j) {
		return (j + 1) & bitMask;
	}

	@Override
	public Iterator<T> iterator() {
		
		return new Iterator<T>() {
			
			int index = 0;
			int counter = 0;

			@Override
			public boolean hasNext() {
				return counter < size;
			}

			@Override
			public T next() {
				while(index < table.length) {
					if(table[index] != null) {
						counter++;
					} else {
						counter++;
					}
				}
				return table[index];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public T get(T key) {
		int i = indexFor(key);
		
		while(table[i] != null && !hasher.equals(table[i], key)) {			
			i = next(i); // mod capacity
		}
		
		return table[i];
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int getInitialCapacity() {
		return initialCapacity;
	}

	@Override
	public int getEnlargeCount() {
		return rehashCount;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean remove(T key) {
		
		if(!contains(key)) {
			return false;
		}
		
		int i = indexFor(key);
		
		while(table[i] != null && !hasher.equals(table[i], key)) {			
			i = next(i); // mod capacity
		}
		
		table[i] = null;
		return true;
	}

	@Override
	public void clear() {
		for(int i = 0; i < table.length; i++) {
			table[i] = null;
		}
	}

	@Override
	public boolean addAll(Iterable<T> c) {
		boolean added = false;
		for(T t : c) {
			added = add(t) == null;
		}
		return added;
	}

}