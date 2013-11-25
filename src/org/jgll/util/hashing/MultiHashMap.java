package org.jgll.util.hashing;

import java.io.Serializable;
import java.util.Iterator;


/**
 * 
 * Interface for hash sets that use multiple hash functions.
 * 
 */
public interface MultiHashMap<K, V> extends Serializable {

	public V get(K key);

	public V put(K key, V value);

	public int size();

	public int getInitialCapacity();

	public int getEnlargeCount();

	public boolean isEmpty();

	public void clear();
	
	public Iterator<K> keyIterator();
	
	public Iterator<V> valueIterator();

}
