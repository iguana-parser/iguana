package org.jgll.util.hashing;

import java.io.Serializable;

public interface ExternalHasher<T> extends Serializable {
	
	public int hash(T t, HashFunction f);
	 
}
