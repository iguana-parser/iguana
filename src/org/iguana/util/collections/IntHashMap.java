package org.iguana.util.collections;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntHashMap<T> implements Serializable, Map<Integer, T> {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.6f;
	
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
	
	public IntHashMap() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public IntHashMap(int initalCapacity) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	@SuppressWarnings("unchecked")
	public IntHashMap(int initialCapacity, float loadFactor) {
		
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
	
	public boolean containsKey(int key) {
		return get(key) != null;
	}
	
	public T putIfAbsent(int key, T value) {
		T val = get(key);
		if (val == null)
			return put(key, value);
		
		return val;
	}
	
	public T put(int key, T value) {
		
		int index = hash(key);

		do {
			if(keys[index] == -1) {
			   keys[index] = key;
			   values[index] = value;
			   size++;
			   if (size >= threshold) {
				  rehash();
			   }
			   return null;
			}
			
			else if(keys[index] == key) {
				return values[index];
			}
			
			collisionsCount++;
			
			index = (index + 1) & bitMask;
			
		} while(true);
	}
	
	public T remove(int key) {
		int index = hash(key);
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
	    	int key = keys[i];
	    	T value = values[i];
	    	
			if(key != -1) {
				
				int index = hash(key);

				do {
					if(newKeys[index] == -1) {
						newKeys[index] = key;
						newValues[index] = value;
						continue label;
					}
					
					index = (index + 1) & bitMask;
					
				} while(true);
			}
	    }
		
		keys = newKeys;
		values = newValues;
		
		threshold = (int) (loadFactor * capacity);
		rehashCount++;
	}
	
	private int hash(int key) {
		return key & bitMask;
	}

	public T get(int key) {
		return values[hash(key)];
	}

	public int size() {
		return size;
	}

	public int getInitialCapacity() {
		return initialCapacity;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public int getEnlargeCount() {
		return rehashCount;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void clear() {
		Arrays.fill(values, -1);
		Arrays.fill(values, null);
		size = 0;
	}

//	public boolean addAll(IntHashMap set) {
//		boolean added = false;
//		IntIterator it = set.iterator();
//		while(it.hasNext()) {
//			added = add(it.next()) == -1;
//		}
//		return added;
//	}
	
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

	@Override
	public boolean containsKey(Object key) {
		return containsKey((int) key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	@Override
	public T get(Object key) {
		return get((int)key);
	}

	@Override
	public T put(Integer key, T value) {
		return put((int)key, value);
	}

	@Override
	public T remove(Object key) {
		return remove((int) key);
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends T> m) {
		m.entrySet().forEach(e -> put(e.getKey(), e.getValue()));
	}

	@Override
	public Set<Integer> keySet() {
		return Arrays.stream(keys).filter(i -> i != -1).boxed().collect(Collectors.toSet());
	}

	@Override
	public Collection<T> values() {
		return Arrays.stream(values).filter(v -> v != null).collect(Collectors.toSet());
	}

	@Override
	public Set<java.util.Map.Entry<Integer, T>> entrySet() {
		return IntStream.range(0, size)
				        .filter(i -> keys[i] != -1)
				        .mapToObj(i -> new IntMapEntry<>(keys[i], values[i]))
				        .collect(Collectors.toSet());
	}
	
	
	private static class IntMapEntry<V> implements java.util.Map.Entry<Integer, V> {

		private final int k;
		private final V v;
		
		public IntMapEntry(int k, V v) { this.k = k; this.v = v; }
		
		@Override
		public Integer getKey() { return k; }

		@Override
		public V getValue() { return v; }

		@Override
		public V setValue(V value) { return v; }
	}
}
