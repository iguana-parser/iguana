package org.jgll.util.hashing;

import java.io.Serializable;
import java.util.Iterator;

import org.jgll.util.RandomUtil;

/**
 * 
 * A hash set based on Cuckoo hashing.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class CuckooHashSet<T> implements Serializable, Iterable<T> {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.4f;
	
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
	
	private Decomposer<T> decomposer;
	
 	@SafeVarargs
	public static <T> CuckooHashSet<T> from(Decomposer<T> decomposer, T...elements) {
		CuckooHashSet<T> set = new CuckooHashSet<>(decomposer);
		for(T e : elements) {
			set.add(e);
		}
		return set;
	}
	
	public CuckooHashSet(Decomposer<T> decomposer) {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, decomposer);
	}
	
	public CuckooHashSet(int initalCapacity, Decomposer<T> decomposer) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR, decomposer);
	}
	
	@SuppressWarnings("unchecked")
	public CuckooHashSet(int initialCapacity, float loadFactor, Decomposer<T> decomposer) {
		this.initialCapacity = initialCapacity;
		this.decomposer = decomposer;
		
		if(initialCapacity < 8) {
			initialCapacity = 8;
		}
		
		this.loadFactor = loadFactor;

		capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        
		threshold = (int) (loadFactor * capacity);

		tableSize = capacity >> 1;
		table1 = (T[]) new Object[tableSize];
		table2 = (T[]) new Object[tableSize];
		
		generateNewHashFunctions();
	}

	private void generateNewHashFunctions() {
		function1 = new MurmurHash3(RandomUtil.random.nextInt(Integer.MAX_VALUE));
		function2 = new MurmurHash3(RandomUtil.random.nextInt(Integer.MAX_VALUE));
	}
	
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
	public T get(T key) {
	
		int index = indexFor(function1.hash(decomposer.toIntArray(key)));
		T value1 = table1[index];
		if(key.equals(value1)) {
			return value1;
		}			
		
		index = indexFor(function2.hash(decomposer.toIntArray(key)));
		T value2 = table2[index];
		if(key.equals(value2)) {
			return value2;
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
	public T add(T key) {
		T e = get(key);
		
		if(e != null) {
			return e;
		}
		
		key = tryInsert(key);
		if(key == null) {
			size++;
			if(size >= threshold) {
				enlargeTables();
			}
			return null;
		}

		// Insertion was unsuccessful
		generateNewHashFunctions();
		rehash();
		return add(key);
	}
	
	protected boolean isEntryEmpty(Object e) {
		return e == null;
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
		int index = indexFor(function1.hash(decomposer.toIntArray(key)));
		if(isEntryEmpty(table1[index])) {
			table1[index] = key;
			return null;
		}
		T tmp = table1[index];
		table1[index] = key;
		key = tmp;
		
		index = indexFor(function2.hash(decomposer.toIntArray(key)));
		if(isEntryEmpty(table2[index])) {
			table2[index] = key;
			return null;
		}
		
		tmp = table2[index];
		table2[index] = key;
		key = tmp;

		
		return key;
	}
	
	private void rehash() {

		mainloop:
		for(int i = 0; i < table1.length; i++) {
			T key = table1[i];
			if(!isEntryEmpty(key)) {
				if(indexFor(function1.hash(decomposer.toIntArray(key))) != i) {
					T tmp = table1[i];
					table1[i] = null;

					tmp = tryInsert(tmp);
					if(tmp == null) {
						continue mainloop;
					}
					
					generateNewHashFunctions();
					rehash();
					break mainloop;
				}
			}
		}
	
		mainloop:
		for(int i = 0; i < table2.length; i++) {
			T key = table2[i];
			if(!isEntryEmpty(key)) {
				if(indexFor(function2.hash(decomposer.toIntArray(key))) != i) {
					T tmp = table2[i];
					table2[i] = null;
	
					tmp = tryInsert(tmp);
					if(tmp == null) {
						continue mainloop;
					}
					
					generateNewHashFunctions();
					rehash();
					break mainloop;
				}
			}
		}
		
		rehashCount++;
	}
	
	@SuppressWarnings("unchecked")
	private void enlargeTables() {
		
		T[] newTable1 = (T[]) new Object[capacity];
		T[] newTable2 = (T[]) new Object[capacity];
		
		tableSize = capacity;
		capacity <<= 1;
		
		threshold = (int) (loadFactor * capacity);
		
		System.arraycopy(table1, 0, newTable1, 0, table1.length);
		System.arraycopy(table2, 0, newTable2, 0, table2.length);
		
		table1 = newTable1;
		table2 = newTable2;
		
		enlargeCount++;
		rehash();
	}
	
	public int size() {
		return size;
	}
	
	public int getInitialCapacity() {
		return initialCapacity;
	}
	
	public int getRehashCount() {
		return rehashCount;
	}
	
	public int getEnlargeCount() {
		return enlargeCount;
	}
	
	protected int indexFor(int hash) {
		return hash & (tableSize - 1);
	}
	
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
					if(!isEntryEmpty(table1[index1])) {
						it++;
						return  table1[index1++];
					}
					index1++;
				}
				
				while(index2 < table2.length) {
					if(!isEntryEmpty(table2[index2])) {
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

	public boolean remove(T key) {
		
		int index = indexFor(function1.hash(decomposer.toIntArray(key)));
		if(key.equals(table1[index])) {
			table1[index] = null;
			size--;
			return true;
		}
		
		index = indexFor(function2.hash(decomposer.toIntArray(key)));
		if(key.equals(table2[index])) {
			table2[index] = null;
			size--;
			return true;
		}

		return false;
	}


	public void clear() {
		for(int i = 0; i < table1.length; i++) {
			table1[i] = null;
			table2[i] = null;
		}
		size = 0;
		rehashCount = 0;
		enlargeCount = 0;
	}
	
	public boolean addAll(Iterable<T> c) {
		boolean changed = false;
		for(T e : c) {
			changed |= (add(e) == null);
		}
		
		return changed;
	}

}
