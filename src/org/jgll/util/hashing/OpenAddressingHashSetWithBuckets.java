package org.jgll.util.hashing;

import java.util.Iterator;

public class OpenAddressingHashSetWithBuckets<T> implements IguanaSet<T> {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 64;
	private static final float DEFAULT_LOAD_FACTOR = 0.5f;
	
	private int initialCapacity;
	
	private int capacity;
	
	private int size;
	
	private int threshold;
	
	private float loadFactor;
	
	private int rehashCount;
	
	private int collisionsCount;
	
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
	
 	@SafeVarargs
	public static <T> OpenAddressingHashSetWithBuckets<T> from(T...elements) {
 		OpenAddressingHashSetWithBuckets<T> set = new OpenAddressingHashSetWithBuckets<>();
		for(T e : elements) {
			set.add(e);
		}
		return set;
	}

	public OpenAddressingHashSetWithBuckets() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public OpenAddressingHashSetWithBuckets(int initalCapacity) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	@SuppressWarnings("unchecked")
	public OpenAddressingHashSetWithBuckets(int initialCapacity, float loadFactor) {
		
		this.initialCapacity = initialCapacity;
		
		this.loadFactor = loadFactor;

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
		
		int index = hash(key);

		do {
			if(table[index] == null) {
				table[index] = key;
				size++;
				if (size >= threshold) {
					rehash();
				}
				return null;
			}
			
			else if(table[index].equals(key)) {
				return table[index];
			}
			
			collisionsCount++;
			
			index = (index + 1) & bitMask;
			
		} while(true);
	}
	
	private void rehash() {
		
		capacity <<= 1;
		p += 1;
		
		bitMask = capacity - 1;
		
		@SuppressWarnings("unchecked")
		T[] newTable = (T[]) new Object[capacity];
		
		label:
		for(T key : table) {
			if(key != null) {
				
				int index = hash(key);

				do {
					if(newTable[index] == null) {
						newTable[index] = key;
						continue label;
					}
					
					index = (index + 1) & bitMask;
					
				} while(true);
			}
		}
		
		table = newTable;
		
		threshold = (int) (loadFactor * capacity);
		rehashCount++;
	}
	
	private int hash(T key) {
		return key.hashCode() & bitMask;
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
						return table[index++];
					} else {
						index++;
					}
				}
				
				throw new RuntimeException("Should not reach here.");
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public T get(T key) {
		
		int index = hash(key);
		
		while(table[index] != null && !table[index].equals(key)) {			
			index = (index + 1) & bitMask;
		}
		
		return table[index];
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
	public int getCapacity() {
		return capacity;
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
	public void clear() {
		for(int i = 0; i < table.length; i++) {
			table[i] = null;
		}
		size = 0;
	}

	@Override
	public boolean addAll(Iterable<T> c) {
		boolean added = false;
		for(T t : c) {
			added = add(t) == null;
		}
		return added;
	}
	
	@Override
	public int getCollisionCount() {
		return collisionsCount++;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		for(T t : table) {
			if(t != null) { 
				sb.append(t).append(", ");
			}
		}
		
		if(sb.length() > 2) {
			sb.delete(sb.length() - 2, sb.length());
		}
		
		sb.append("}");
		return sb.toString();
	}
	
}
