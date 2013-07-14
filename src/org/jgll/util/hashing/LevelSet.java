package org.jgll.util.hashing;


public class LevelSet<T extends Level> extends CuckooHashSet<T> {

	private static final long serialVersionUID = 1L;
	
	private int level;
	
	public LevelSet() {	}
	
	public LevelSet(int initalCapacity) {
		super(initalCapacity);
	}
	
	@Override
	public boolean isEntryEmpty(Object o) {
		Level node = (Level) o;
		return o == null || node.getLevel() != level;
	}
	
	@Override
	public boolean add(T key) {
		level = key.getLevel();
		return super.add(key);
	}
	
	@Override
	public T addAndGet(T key) {
		level = key.getLevel();
		return super.addAndGet(key);
	}

	@Override
	public void clear() {
		size = 0;
		rehashCount = 0;
		enlargeCount = 0;
	}
	
}
