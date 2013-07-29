package org.jgll.util.hashing;

public interface ExternalHasher<T> {
	
	public int hash(T t, HashFunction f);
	 
}
