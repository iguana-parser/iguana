package org.jgll.util.hashing;

import java.io.Serializable;



public class LevelMap<K extends Level & HashKey, V> implements Serializable {

	private static final long serialVersionUID = 1L;

	private LevelSet<MapEntry<K, V>> set;
	
	public LevelMap() {	
		set = new LevelSet<>();
	}
	
	public LevelMap(int initalCapacity) {
		set = new LevelSet<>(initalCapacity);
	}
		
	public V put(K key, V value) {
		MapEntry<K, V> entry = set.add(new MapEntry<K, V>(key, value));
		if(entry == null) {
			return null;
		}
		return entry.v;
	}
	
	public V get(K key) {
		MapEntry<K, V> entry = set.get(new MapEntry<K, V>(key, null));
		if(entry != null) {
			return entry.getValue();
		}
		
		return null;
	}

	public void clear() {
		set.clear();
	}
	
	public static class MapEntry<K extends HashKey & Level, V> implements HashKey, Level {
		
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

		@Override
		public int getLevel() {
			return k.getLevel();
		}
	}

	
}
