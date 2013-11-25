package org.jgll.util.hashing;

public class HashTableFactory {
	
	public static final int CUCKOO = 0;
	public static final int OPEN_ADDRESSING = 1;
	public static final int CHAINING = 2;
	
	private int type;
	
	private static HashTableFactory instance;
	
	public static void init(int type) {
		instance = new HashTableFactory(type);
	}
	
	public static HashTableFactory getFactory() {
		if(instance == null) {
			throw new IllegalStateException("Factory is not initialized yet.");
		}
		return instance;
	}

	
	private HashTableFactory(int type) {
		this.type = type;
	}

	public <T> MultiHashSet<T> newHashSet(int initialSize, ExternalHasher<T> hasher) {
		if(type == CUCKOO) {
			return new CuckooHashSet<>(initialSize, hasher);
		} 
		else if (type == OPEN_ADDRESSING) {
			return new OpenAddressingHashSet<>(initialSize, hasher);
		} 
		else {
			throw new RuntimeException("Not implemented yet.");
		}
	}
	
	public <T> MultiHashSet<T> newHashSet(ExternalHasher<T> hasher) {
		if(type == CUCKOO) {
			return new CuckooHashSet<>(hasher);
		} 
		else if (type == OPEN_ADDRESSING) {
			return new OpenAddressingHashSet<>(hasher);
		} 
		else {
			throw new RuntimeException("Not implemented yet.");
		}
	}	

}
