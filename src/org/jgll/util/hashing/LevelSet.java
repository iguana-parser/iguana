package org.jgll.util.hashing;


public class LevelSet<T extends Level> extends CuckooHashSet<T> {

	private static final long serialVersionUID = 1L;
	
	private int level;
	
	public LevelSet(ExternalHasher<T> decomposer) {
		super(decomposer);
	}
	
	public LevelSet(int initalCapacity, ExternalHasher<T> decomposer) {
		super(initalCapacity, decomposer);
	}
	
	@Override
	public boolean isEntryEmpty(T t) {
		return t == null || t.getLevel() != level;
	}
	
	@Override
	public T add(T key) {
		level = key.getLevel();
		return super.add(key);
	}
	
	@Override
	public void clear() {
		size = 0;
		rehashCount = 0;
		enlargeCount = 0;
	}
	
}
