package iguana.utils.collections;


import java.util.function.IntFunction;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface IntHashMap<T> extends Iterable<Entry<T>> {
	
	public boolean containsKey(int key);
	
	public T computeIfAbsent(int key, IntFunction<T> f);
	
	public T compute(int key, IntKeyMapper<T> mapper);
	
	public T put(int key, T value);
	
	public T remove(int key);
		
	public T get(int key);

	public int size();
	
	public int getInitialCapacity();
	
	public int getEnlargeCount();

	public boolean isEmpty();

	public void clear();

	public int getCollisionCount();
	
	public Iterable<T> values();
	
}

