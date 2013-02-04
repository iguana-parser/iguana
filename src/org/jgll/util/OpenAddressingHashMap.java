package org.jgll.util;

import java.util.Collection;
import org.jgll.util.KeyValue;
import java.util.Map;
import java.util.Set;

public class OpenAddressingHashMap<K, V> implements Map<K, V> {
	
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	private Entry<K, V>[] table;
	
	private int capacity;
	
	private int threshold;
	
	private final float loadFactor;
	
	private int size;
	
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
	
	public OpenAddressingHashMap() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public OpenAddressingHashMap(int initialCapcity) {
		this(initialCapcity, DEFAULT_LOAD_FACTOR);
	}
	
	@SuppressWarnings("unchecked")
	public OpenAddressingHashMap(int initialCapacity, float loadFactor) {
		this.loadFactor = loadFactor;
		
		capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
            p++;
        }
		
		bitMask = capacity - 1;
		
		threshold = (int) (loadFactor * capacity);
		this.table = new Entry[capacity];
	}
	
	@Override
	public V get(Object key) {
		
		int j = indexFor(key.hashCode());
		
		do {
			if(table[j] == null) {
				return null;
			}
			
			if(table[j].getKey().equals(key)) {
				return table[j].getValue();
			}
			
			j = next(j);
			
		} while(true);
	}
	
	public V put(K key, V value) {		
		int j = indexFor(key.hashCode());
		
		do {
			if(table[j] == null) {
				table[j] = new KeyValue<>(key, value);
				size++;
				if (size >= threshold) {
					rehash();
				}
				return null;
			}
			
			else if(table[j].getKey().equals(key)) {
				V oldValue = table[j].getValue();
				table[j].setValue(value);
				return oldValue;
			}
			
			j = next(j);
		} while(true);
	}
	
	public void delete(K key) {
		throw new UnsupportedOperationException();
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	private void rehash() {
		capacity <<= 1;
		p += 1;
		bitMask = capacity - 1;
		
		Entry<K, V>[] newTable = new KeyValue[capacity];
		
		label:
		for(Entry<K, V> entry : table) {
			if(entry != null) {
				
				int j = indexFor(entry.getKey().hashCode());
				
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
	
	private int indexFor(int hash) {
		return (hash >> (32 - p)) & bitMask;		
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
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		table = new Entry[capacity];
		size = 0;
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

}
