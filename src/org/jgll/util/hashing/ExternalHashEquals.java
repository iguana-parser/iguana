package org.jgll.util.hashing;


public interface ExternalHashEquals<T> {
	
	public int hash(T t);
	
	public boolean equals(T t1, T t2);
}
