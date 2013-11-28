package org.jgll.util.hashing;

import java.io.Serializable;


/**
 * 
 * Interface for hash sets that use multiple hash functions.
 * 
 */
public interface MultiHashSet<T> extends Serializable, Iterable<T> {

	public boolean contains(T key);

	public T get(T key);

	public T add(T key);

	public int size();

	public int getInitialCapacity();
	
	public int getCapacity();

	public int getEnlargeCount();
	
	public int getCollisionCount();

	public boolean isEmpty();

	public void clear();

	public boolean addAll(Iterable<T> c);

}
