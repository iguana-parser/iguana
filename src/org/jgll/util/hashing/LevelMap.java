package org.jgll.util.hashing;

import java.io.Serializable;


public class LevelMap<K extends Level, V> implements Serializable {

	private static final long serialVersionUID = 1L;

	private LevelSet<MapEntry<K, V>> set;
	
	public LevelMap(ExternalHasher<K> decomposer) {	
		set = new LevelSet<>(new MapEntryExternalHasher(decomposer));
	}
	
	public LevelMap(int initalCapacity, ExternalHasher<K> decomposer) {
		set = new LevelSet<>(initalCapacity, new MapEntryExternalHasher(decomposer));
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
	
	public static class MapEntry<K extends Level, V> implements Level {
		
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
		public int getLevel() {
			return k.getLevel();
		}
	}
	
	public class MapEntryExternalHasher implements ExternalHasher<MapEntry<K, V>> {

		private static final long serialVersionUID = 1L;
		
		private ExternalHasher<K> externalHasher;

		public MapEntryExternalHasher(ExternalHasher<K> externalHasher) {
			this.externalHasher = externalHasher;
		}
		
		@Override
		public int hash(MapEntry<K, V> e, HashFunction f) {
			return externalHasher.hash(e.k, f);
		}

		@Override
		public boolean equals(MapEntry<K, V> e1, MapEntry<K, V> e2) {
			return externalHasher.equals(e1.k, e2.k);
		}
	}
	
}
