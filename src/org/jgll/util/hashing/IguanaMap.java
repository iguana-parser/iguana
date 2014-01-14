package org.jgll.util.hashing;

public interface IguanaMap<K, V> {

	public boolean containsKey(K key);

	public V get(K k);

	public V put(K k, V v);

	public int size();

	public int getInitialCapacity();

	public int getCapacity();

	public int getRehashCount();

	public boolean isEmpty();

	public void clear();

}
