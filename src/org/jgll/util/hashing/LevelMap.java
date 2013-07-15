package org.jgll.util.hashing;


public class LevelMap<K extends Level, V> extends CuckooHashMap<K, V> {

	private static final long serialVersionUID = 1L;
	
	private int level;
	
	public LevelMap() {	}
	
	public LevelMap(int initalCapacity) {
		super(initalCapacity);
	}
	
	@Override
	public boolean isEntryEmpty(Entry<K, V> o) {
		return o == null || o.getKey().getLevel() != level;
	}
	
	@Override
	public V put(K key, V value) {
		level = key.getLevel();
		return super.put(key, value);
	}

	@Override
	public void clear() {
		size = 0;
		rehashCount = 0;
		enlargeCount = 0;
	}
	
}
