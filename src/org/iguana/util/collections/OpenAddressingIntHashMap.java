package org.iguana.util.collections;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.IntFunction;

public class OpenAddressingIntHashMap<T> implements IntHashMap<T>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 8;
	private static final float DEFAULT_LOAD_FACTOR = 0.4f;
	
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
	
	private int[] keys;
	
	private T[] values;
	
	private HashFunction linearProbing = (k, j) -> (k + j) & bitMask;

	private HashFunction doubleHashing = (k, j) -> (k + j + 1 + (k % 7)) & bitMask;
	
	private HashFunction hash = doubleHashing;
	
	public OpenAddressingIntHashMap() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public OpenAddressingIntHashMap(int initalCapacity) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	@SuppressWarnings("unchecked")
	public OpenAddressingIntHashMap(int initialCapacity, float loadFactor) {
		
		this.initialCapacity = initialCapacity;
		
		this.loadFactor = loadFactor;

		capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        
		bitMask = capacity - 1;
		
		threshold = (int) (loadFactor * capacity);
		keys = new int[capacity];
		values = (T[]) new Object[capacity];
		
		Arrays.fill(keys, -1);
	}
	
	@Override
	public boolean containsKey(int key) {
		return get(key) != null;
	}
	
	@Override
	public T computeIfAbsent(int key, IntFunction<T> f) {
		int j = 0;
		int index = hash.apply(key, j);

		do {
			if (keys[index] == -1) {
			    keys[index] = key;
			    T val = f.apply(key);
			    values[index] = val;
			    size++;
			    if (size >= threshold) {
			    	rehash();
			    }
			    return val;
			}
			
			else if(keys[index] == key) {
				return values[index];
			}
			
			collisionsCount++;
			
			index = hash.apply(key, ++j);
			
		} while(true);
	}
	
	@Override
	public T put(int key, T value) {
		
		int j = 0;
		int index = hash.apply(key, j);

		do {
			if (keys[index] == -1) {
				keys[index] = key;
				values[index] = value;
				size++;
				if (size >= threshold) {
					rehash();
				}
				return null;
			}
			else if (keys[index] == key) {
				return values[index];
			}
			
			collisionsCount++;
			
			index = hash.apply(key, ++j);
			
		} while(true);
	}
	
	@Override
	public T remove(int key) {
		int j = 0;
		int index = hash.apply(key, j);
		
		while (keys[index] != -1 && keys[index] != key) {
			index = hash.apply(key, ++j);
		}
		
		T v = values[index];
		values[index] = null;
		keys[index] = -1;
		return v;
	}
	
	private void rehash() {
		
		capacity <<= 1;
		
		bitMask = capacity - 1;
		
		int[] newKeys = new int[capacity];
		Arrays.fill(newKeys, -1);
		
		@SuppressWarnings("unchecked")
		T[] newValues = (T[]) new Object[capacity];
		
		label:
	    for(int i = 0; i < keys.length; i++) {
	    	int j = 0;
	    	int key = keys[i];
	    	
	    	T value = values[i];
	    	
			if(key != -1) {
				
				int index = hash.apply(key, j);

				do {
					if(newKeys[index] == -1) {
						newKeys[index] = key;
						newValues[index] = value;
						continue label;
					}
					
					index = hash.apply(key, ++j);
					
				} while(true);
			}
	    }
		
		keys = newKeys;
		values = newValues;
		
		threshold = (int) (loadFactor * capacity);
		rehashCount++;
	}
		
	@Override
	public T get(int key) {
		int j = 0;
		int index = hash.apply(key, j);
		while (keys[index] != -1 && keys[index] != key) {
			index = hash.apply(key, ++j);
		}
		return values[index];
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
	public void clear() {
		Arrays.fill(values, -1);
		Arrays.fill(values, null);
		size = 0;
	}

	public int getCollisionCount() {
		return collisionsCount++;
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		for(int t : keys) {
			if(t != -1) { 
				sb.append(t).append(", ");
			}
		}
		
		if(sb.length() > 2) {
			sb.delete(sb.length() - 2, sb.length());
		}
		
		sb.append("}");
		return sb.toString();
	}
	
	@FunctionalInterface
	static interface HashFunction {
		int apply(int k, int j);
	}

	@Override
	public Iterable<T> values() {
//		return new Iterator<T>() {
//			
//			int i = 0;
//			int j = 0;
//
//			@Override
//			public boolean hasNext() {
//				return i < size;
//			}
//
//			@Override
//			public T next() {
//				while (values[j] != null) j++;
//				i++;
//				return values[j];
//			}
//		};
		return null;
	}

	@Override
	public Iterator<Entry<T>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
