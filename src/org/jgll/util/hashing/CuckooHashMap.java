package org.jgll.util.hashing;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 
 * A hash set based on Cuckoo hashing.
 * 
 * @author Ali Afroozeh
 *
 */
public class CuckooHashMap<K, V> implements Map<K, V>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 32;
	private static final float DEFAULT_LOAD_FACTOR = 0.4f;
	
	private int initialCapacity;
	
	/**
	 * The size of this hash table. Since in Cuckoo hashing we use two tables,
	 * capacity is double the size of each tables.
	 */
	private int capacity;
	
	/**
	 * p = log2 capacity
	 */
	private int p;
	
	/**
	 * How many elements are currently in the hash tables
	 */
	protected int size;
	
	/**
	 * The maximum number of elements in tables before an enlarge operation is
	 * carried out.
	 */
	private int threshold;
	
	private float loadFactor;
	
	/**
	 * How many times the rehash method is called. 
	 */
	protected int rehashCount;
	
	/**
	 * How many times the tables are enlarged.
	 */
	protected int enlargeCount;
	
	private UniversalHashFunction function1;
	
	private UniversalHashFunction function2;
	
	protected Entry<K, V>[] table1;

	protected Entry<K, V>[] table2;
			
	public CuckooHashMap() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public CuckooHashMap(int initalCapacity) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	@SuppressWarnings("unchecked")
	public CuckooHashMap(int initialCapacity, float loadFactor) {
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
		table1 = new MapEntry[tableSize];
		table2 = new MapEntry[tableSize];
		
		generateNewHashFunctions();
	}

	private void generateNewHashFunctions() {
		function1 = new MultiplicationShiftPlainUniversalHashFunction(p);
		function2 = new MultiplicationShiftPlainUniversalHashFunction(p);
	}
	
	@Override
	public V get(Object key) {
	
		int index = hash1(key);
		Entry<K, V> value1 = table1[index];
		
		if(value1 != null && key.equals(value1.getKey())) {
			return value1.getValue();
		}			
		
		index = hash2(key);
		Entry<K, V> value2 = table2[index];
		
		if(value2 != null && key.equals(value2.getKey())) {
			return value2.getValue();
		}

		return null;
	}
	
	private boolean insertAgain(Entry<K, V> e, Entry<K, V>[] table1, Entry<K, V>[] table2) {
		int i = 0;
		while(i < size + 1) {
			i++;

			int index = hash1(e.getKey());
			if(isEntryEmpty(table1[index])) {
				table1[index] = e;
				return true;
			}
			
			Entry<K, V> tmp = table1[index];
			table1[index] = e;
			e = tmp;
			
			index = hash2(e.getKey());
			if(isEntryEmpty(table2[index])) {
				table2[index] = e;
				return true;
			}
			
			tmp = table2[index];
			table2[index] = e;
			e = tmp;
		}
		return false;
	}
	
	
	@Override
	public V put(K key, V value) {
		V v = get(key);
		
		if(v != null) {
			return v;
		}
		
		Entry<K, V> entry = new MapEntry<>(key, value);
		
		int i = 0;
		while(i < size + 1) {
			i++;
			
			int index = hash1(entry.getKey());
			if(isEntryEmpty(table1[index])) {
				table1[index] = entry;
				size++;
				if(size >= threshold) {
					enlargeTables();
				}
				return null;
			}
			
			Entry<K, V> tmp = table1[index];
			table1[index] = entry;
			entry = tmp;
			
			index = hash2(entry.getKey());
			if(isEntryEmpty(table2[index])) {
				table2[index] = entry;
				size++;
				if(size >= threshold) {
					enlargeTables();
				}
				return null;
			}
			
			tmp = table2[index];
			table2[index] = entry;
			entry = tmp;
		}
		
		rehash();
		return put(entry.getKey(), entry.getValue());
	}
	
	protected boolean isEntryEmpty(Object e) {
		return e == null;
	}
	
	@SuppressWarnings("unchecked")
	private void rehash() {
		Entry<K, V>[] newTable1 = new Entry[table1.length];
		Entry<K, V>[] newTable2 = new Entry[table2.length];

		generateNewHashFunctions();
		
		for(Entry<K, V> e : table1) {
			if(!isEntryEmpty(e)) {
				// if one element cannot be inserted, restart the whole process with two new
				// hash function.
				if(!insertAgain(e, newTable1, newTable2)) {
					rehash();
					return;
				}
			}
		}
		
		for(Entry<K, V> e : table2) {
			if(!isEntryEmpty(e)) {
				// if one element cannot be inserted, restart the whole process with two new
				// hash function.
				if(!insertAgain(e, newTable1, newTable2)) {
					rehash();
					return;
				}
			}
		}
		
		table1 = newTable1;
		table2 = newTable2;
		
		rehashCount++;
	}
	
	@SuppressWarnings("unchecked")
	private void enlargeTables() {
		
		Entry<K, V>[] newTable1 = new Entry[capacity];
		Entry<K, V>[] newTable2 = new Entry[capacity];
		
		capacity <<= 1;
		p++;
		
		threshold = (int) (loadFactor * capacity);
		
		int i = 0;
		for(Entry<K, V> e : table1) {
			if(!isEntryEmpty(e)) {
				newTable1[i++] = e;
			}
		}
		
		for(Entry<K, V> e : table2) {
			if(!isEntryEmpty(e)) {
				newTable1[i++] = e;
			}
		}
		
		table1 = newTable1;
		table2 = newTable2;
		
		enlargeCount++;
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
	
	public int getEnlargeCount() {
		return enlargeCount;
	}
	
	protected int hash1(Object key) {
		return function1.hash(key.hashCode());
	}
	
	protected int hash2(Object key) {
		return function2.hash(key.hashCode());
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public V remove(Object key) {
		
		int index = hash1(key);
		Entry<K, V> value1 = table1[index];

		if(value1 != null && key.equals(value1.getKey())) {
			V value = table1[index].getValue();
			table1[index] = null;
			size--;
			return value;
		}
		
		index = hash2(key);
		Entry<K, V> value2 = table2[index];
		if(value2 != null && key.equals(value2.getKey())) {
			V value = table1[index].getValue();
			table2[index] = null;
			size--;
			return value;
		}

		return null;
	}

	@Override
	public void clear() {
		for(int i = 0; i < table1.length; i++) {
			table1[i] = null;
			table2[i] = null;
		}
		size = 0;
		rehashCount = 0;
		enlargeCount = 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public static class MapEntry<K, V> implements Entry<K, V> {
		
		private K k;
		private V v;

		public MapEntry(K k, V v) {
			this.k = k;
			this.v = v;
		}

		@Override
		public K getKey() {
			return k;
		}

		@Override
		public V getValue() {
			return v;
		}

		@Override
		public V setValue(V value) {
			this.v = value;
			return v;
		}
		
	}


}
