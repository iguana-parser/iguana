package org.jgll.util.hashing;

import java.util.Iterator;

import org.jgll.parser.HashFunctions;
import org.jgll.util.RandomUtil;
import org.jgll.util.hashing.hashfunction.HashFunction;
import org.jgll.util.hashing.hashfunction.SimpleTabulation8;

/**
 * 
 * A hash set based on Cuckoo hashing.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class CuckooHashSet<T> implements IguanaSet<T> {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.49f;
	
	private int initialCapacity;
	
	/**
	 * The size of this hash table. Since in Cuckoo hashing we use two tables,
	 * capacity is double the size of each tables.
	 */
	private int capacity;
	
	/**
	 * How many elements are currently in the hash tables
	 */
	protected int size;
	
	/**
	 * The maximum number of elements in tables before an enlarge operation is
	 * carried out.
	 */
	private int threshold;
	
	private float loadFactor;
	
	/**
	 * How many times the rehash method is called. 
	 */
	protected int rehashCount;
	
	/**
	 * How many times the tables are enlarged.
	 */
	protected int enlargeCount;
	
	private HashFunction function1;
	
	private HashFunction function2;
	
	protected T[] table1;

	protected T[] table2;

	protected int tableSize;
	
	private ExternalHasher<T> externalHasher;
	
	private int collisionsCount;
	
 	@SafeVarargs
	public static <T> CuckooHashSet<T> from(ExternalHasher<T> hasher, T...elements) {
		CuckooHashSet<T> set = new CuckooHashSet<>(hasher);
		for(T e : elements) {
			set.add(e);
		}
		return set;
	}
	
	public CuckooHashSet(ExternalHasher<T> hasher) {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, hasher);
	}
	
	public CuckooHashSet(int initalCapacity, ExternalHasher<T> hasher) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR, hasher);
	}
	
	@SuppressWarnings("unchecked")
	public CuckooHashSet(int initialCapacity, float loadFactor, ExternalHasher<T> hasher) {
		this.initialCapacity = initialCapacity;
		this.externalHasher = hasher;
		
		if(initialCapacity < 8) {
			initialCapacity = 8;
		}
		
		this.loadFactor = loadFactor;

		capacity = 1;
        while (capacity < initialCapacity) {
            capacity *= 2;
        }
        
		threshold = (int) (loadFactor * capacity);

		tableSize = capacity / 2;
		table1 = (T[]) new Object[tableSize];
		table2 = (T[]) new Object[tableSize];
		
		generateNewHashFunctions();
	}
	
	private void generateNewHashFunctions() {
		function1 = HashFunctions.murmurHash3(RandomUtil.random.nextInt(Integer.MAX_VALUE));
		function2 = HashFunctions.murmurHash3(RandomUtil.random.nextInt(Integer.MAX_VALUE));
	}
	
	@Override
	public boolean contains(T key) {
		return get(key) != null;
	}
	
	/**
	 * 
	 * Returns the reference to the element stored in the set which is 
	 * the same as the passed key.
	 * 
	 * @param key
	 * 
	 * @return null if no element matching the given key is found.
	 */
	@Override
	public T get(T key) {
	
		int index = indexFor(externalHasher.hash(key, function1));
		
		T value = table1[index];
		if(value != null && externalHasher.equals(key, value)) {
			return value;
		}			
		
		index = indexFor(externalHasher.hash(key, function2));
		value = table2[index];
		if(value != null && externalHasher.equals(key, value)) {
			return value;
		}

		return null;
	}
	
	/**
	 * 
	 * Adds the given key to the set if it does not already exist in the set.
	 * 
	 * @param key 
	 * @return A reference to the old key stored in the set if the key was in 
	 *         the set, otherwise returns null. 
	 */
	@Override
	public T add(T key) {
		
		if(size >= threshold) {
			enlargeTables();
		}
		
		T e = get(key);
		
		if(e != null) {
			return e;
		}
		
		key = tryInsert(key);

		while(key != null) {
			generateNewHashFunctions();
			rehash();
			key = tryInsert(key);
		}

		size++;
		return null;
	}
		
	private T tryInsert(T key) {
		int i = 0;
		while(i < size + 1) {
			i++;
			
			key = insert(key);
			if(key == null) {
				return null;
			}
		}
		
		return key;
	}
	
	/**
	 * 
	 * Inserts the key into the first table. If the intended cell is full,
	 * the current entry is replaced by the new entry and and old one is 
	 * tried to be placed in the second table.   
	 * 
	 * @param key
	 * 
	 * @return null if an empty spot could be found in the first or the second table.
	 *              Otherwise, the existing key in the second table is returned.
	 */
	private T insert(T key) {
		int index = indexFor(externalHasher.hash(key, function1));
		if(table1[index] == null) {
			table1[index] = key;
			return null;
		}
		
		collisionsCount++;
		
		T tmp = table1[index];
		table1[index] = key;
		key = tmp;
		
		index = indexFor(externalHasher.hash(key, function2));
		if(table2[index] == null) {
			table2[index] = key;
			return null;
		}
		
		collisionsCount++;
		
		tmp = table2[index];
		table2[index] = key;
		key = tmp;
		
		return key;
	}
	
	private void rehash() {
		
		rehashCount++;

		table1Loop:
		for(int i = 0; i < table1.length; i++) {
			T key = table1[i];
			if(key != null) {
				if(indexFor(externalHasher.hash(key, function1)) != i) {
					T tmp = table1[i];
					table1[i] = null;

					tmp = tryInsert(tmp);
					if(tmp == null) {
						continue table1Loop;
					}
					
					// If the control flow reaches here, it means that the insertion in 
					// the rehash has failed, and we need a new set of hash functions.
					// We put the tmp back into an empty spot in the table and start again.
					putInEmptySlot(tmp);
					
					generateNewHashFunctions();
					rehash();
					return;
				}
			}
		}
	
		table2Loop:
		for(int i = 0; i < table2.length; i++) {
			T key = table2[i];
			if(key != null) {
				if(indexFor(externalHasher.hash(key, function2)) != i) {
					T tmp = table2[i];
					table2[i] = null;
	
					tmp = tryInsert(tmp);
					if(tmp == null) {
						continue table2Loop;
					}
					
					putInEmptySlot(tmp);

					generateNewHashFunctions();
					rehash();
					return;
				}
			}
		}
	}
	
	private void putInEmptySlot(T key) {
		for(int i = 0; i < table1.length; i++) {
			if(table1[i] == null) {
				table1[i] = key;
				return;
			}
		}
		for(int i = 0; i < table2.length; i++) {
			if(table2[i] == null) {
				table2[i] = key;
				return;
			}
		}
		
		throw new IllegalStateException("Shouldn't reach here");
	}
	
	@SuppressWarnings("unchecked")
	private void enlargeTables() {
		
		T[] newTable1 = (T[]) new Object[capacity];
		T[] newTable2 = (T[]) new Object[capacity];
		
		tableSize = capacity;
		capacity *= 2;
		
		threshold = (int) (loadFactor * capacity);
		
		System.arraycopy(table1, 0, newTable1, 0, table1.length);
		System.arraycopy(table2, 0, newTable2, 0, table2.length);
		
		table1 = newTable1;
		table2 = newTable2;
		
		enlargeCount++;
		rehash();
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public int getInitialCapacity() {
		return initialCapacity;
	}
	
	@Override
	public int getCapacity() {
		return capacity;
	}
	
	public int getRehashCount() {
		return rehashCount;
	}

	@Override
	public int getEnlargeCount() {
		return enlargeCount;
	}
	
	protected int indexFor(int hash) {
		return hash & (tableSize - 1);
	}
	
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<T> iterator() {

		return new Iterator<T>() {

			int it = 0;
			int index1 = 0;
			int index2 = 0;
			
			@Override
			public boolean hasNext() {
				return it < size;
			}

			@Override
			public T next() {
				while(index1 < table1.length) {
					if(table1[index1] != null) {
						it++;
						return  table1[index1++];
					}
					index1++;
				}
				
				while(index2 < table2.length) {
					if(table2[index2] != null) {
						it++;
						return table2[index2++];
					}
					index2++;
				}
				
				throw new RuntimeException("There is no next.");
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	@Override
	public void clear() {
		for(int i = 0; i < table1.length; i++) {
			table1[i] = null;
			table2[i] = null;
		}
		size = 0;
		rehashCount = 0;
		enlargeCount = 0;
	}
	
	@Override
	public boolean addAll(Iterable<T> c) {
		boolean changed = false;
		for(T e : c) {
			changed |= (add(e) == null);
		}
		
		return changed;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for(T t : table1) {
			if(t != null) 
				sb.append(t).append(", ");
		}
		for(T t : table2) {
			if(t != null)
				sb.append(t).append(", ");
		}
		
		if(sb.length() > 2)
			sb.delete(sb.length() - 2, sb.length());
		
		sb.append("}");
		return sb.toString();
	}

	@Override
	public int getCollisionCount() {
		return collisionsCount;
	}

}