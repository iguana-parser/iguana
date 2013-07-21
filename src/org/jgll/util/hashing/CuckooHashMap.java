package org.jgll.util.hashing;

import java.io.Serializable;

/**
 * 
 * A hash set based on Cuckoo hashing.
 * 
 * @author Ali Afroozeh
 *
 */
public class CuckooHashMap<K extends HashKey, V> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private CuckooHashSet<MapEntry<K, V>> set;
	
	public CuckooHashMap(int initialCapacity) {
		set = new CuckooHashSet<>(initialCapacity);
	}
	
	public CuckooHashMap() {
		set = new CuckooHashSet<>();
	}
	
	public V get(K key) {
		MapEntry<K, V> entry = set.get(new MapEntry<K, V>(key, null));
		if(entry != null) {
			return entry.getValue();
		}
		
		return null;
	}
	
	public V put(K key, V value) {
		MapEntry<K, V> add = set.add(new MapEntry<K, V>(key, value));
		if(add == null) {
			return null;
		}
		
		return add.v;
	}
	
	public int size() {
		return set.size();
	}
	
	public void clear() {
		set.clear();
	}
	
	public static class MapEntry<K extends HashKey, V> implements HashKey {
		
		private K k;
		private V v;
		
		public MapEntry(K k, V v) {
			this.k = k;
			this.v = v;
		}

		public K getKey() {
			return k;
		}

		public V getValue() {
			return v;
		}

		public V setValue(V value) {
			this.v = value;
			return v;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			
			if(! (obj instanceof MapEntry)) {
				return false;
			}
			
			@SuppressWarnings("unchecked")
			MapEntry<K, V> other = (MapEntry<K, V>) obj;
			
			return k.equals(other.k);
		}

		@Override
		public int hash(HashFunction f) {
			return k.hash(f);
		}
	}


}
