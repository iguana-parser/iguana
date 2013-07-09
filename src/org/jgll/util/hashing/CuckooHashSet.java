package org.jgll.util.hashing;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * A hash set based on Cuckoo hashing.
 * 
 * @author Ali Afroozeh
 *
 */
public class CuckooHashSet<E> implements Set<E>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 32;
	private static final float DEFAULT_LOAD_FACTOR = 0.4f;
	
	private int initialCapacity;
	
	/**
	 * The size of this hash table. Since in Cuckoo hashing we use two tables,
	 * capacity is double the size of each tables.
	 */
	private int capacity;
	
	/**
	 * p = log2 capacity
	 */
	private int p;
	
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
	
	private UniversalHashFunction function1;
	
	private UniversalHashFunction function2;
	
	protected Object[] table1;

	protected Object[] table2;
		
	@SafeVarargs
	public static <T> CuckooHashSet<T> from(T...elements) {
		CuckooHashSet<T> set = new CuckooHashSet<>();
		for(T e : elements) {
			set.add(e);
		}
		return set;
	}
	
	public CuckooHashSet() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public CuckooHashSet(int initalCapacity) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	public CuckooHashSet(int initialCapacity, float loadFactor) {
		this.initialCapacity = initialCapacity;
		
		if(initialCapacity < 8) {
			initialCapacity = 8;
		}
		
		this.loadFactor = loadFactor;

		capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
            p++;
        }
        
		threshold = (int) (loadFactor * capacity);

        // For each table
		p--;
		
		int tableSize = capacity >> 1;
		table1 = new Object[tableSize];
		table2 = new Object[tableSize];
		
		generateNewHashFunctions();
	}

	private void generateNewHashFunctions() {
		function1 = new MultiplicationShiftPlainUniversalHashFunction(p);
		function2 = new MultiplicationShiftPlainUniversalHashFunction(p);
	}
	
	@Override
	public boolean contains(Object key) {
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
	@SuppressWarnings("unchecked")
	public E get(Object key) {
	
		int index = hash1(key);
		Object value1 = table1[index];
		if(key.equals(value1)) {
			return (E) value1;
		}			
		
		index = hash2(key);
		Object value2 = table2[index];
		if(key.equals(value2)) {
			return (E) value2;
		}

		return null;
	}
	
	private boolean insertAgain(Object key, Object[] table1, Object[] table2) {
		int i = 0;
		while(i < size + 1) {
			i++;

			int index = hash1(key);
			if(isEntryEmpty(table1[index])) {
				table1[index] = key;
				return true;
			}
			
			Object tmp = table1[index];
			table1[index] = key;
			key = tmp;
			
			index = hash2(key);
			if(isEntryEmpty(table2[index])) {
				table2[index] = key;
				return true;
			}
			
			tmp = table2[index];
			table2[index] = key;
			key = tmp;
		}
		return false;
	}
	
	@Override
	public boolean add(E key) {
		return addAndGet(key) == null;
	}
	
	/**
	 * 
	 * Adds the given key to the set if it does not already exist in the set.
	 * 
	 * @param key 
	 * @return A reference to the old key stored in the set if the key was in 
	 *         the set, otherwise returns null. 
	 */
	@SuppressWarnings("unchecked")
	public E addAndGet(E key) {
		E e = get(key);
		
		if(e != null) {
			return e;
		}
		
		int i = 0;
		while(i < size + 1) {
			i++;
			
			int index = hash1(key);
			if(isEntryEmpty(table1[index])) {
				table1[index] = key;
				size++;
				if(size >= threshold) {
					enlargeTables();
				}
				return null;
			}
			
			E tmp = (E) table1[index];
			table1[index] = key;
			key = tmp;
			
			index = hash2(key);
			if(isEntryEmpty(table2[index])) {
				table2[index] = key;
				size++;
				if(size >= threshold) {
					enlargeTables();
				}
				return null;
			}
			
			tmp = (E) table2[index];
			table2[index] = key;
			key = tmp;
		}
		
		rehash();
		return addAndGet(key);
	}
	
	protected boolean isEntryEmpty(Object e) {
		return e == null;
	}
	
	private void rehash() {
		Object[] newTable1 = new Object[table1.length];
		Object[] newTable2 = new Object[table2.length];

		generateNewHashFunctions();
		
		for(Object key : table1) {
			if(!isEntryEmpty(key)) {
				// if one element cannot be inserted, restart the whole process with two new
				// hash function.
				if(!insertAgain(key, newTable1, newTable2)) {
					rehash();
					return;
				}
			}
		}
		
		for(Object key : table2) {
			if(!isEntryEmpty(key)) {
				// if one element cannot be inserted, restart the whole process with two new
				// hash function.
				if(!insertAgain(key, newTable1, newTable2)) {
					rehash();
					return;
				}
			}
		}
		
		table1 = newTable1;
		table2 = newTable2;
		
		rehashCount++;
	}
	
	private void enlargeTables() {
		
		Object[] newTable1 = new Object[capacity];
		Object[] newTable2 = new Object[capacity];
		
		capacity <<= 1;
		p++;
		
		threshold = (int) (loadFactor * capacity);
		
		int i = 0;
		for(Object key : table1) {
			if(!isEntryEmpty(key)) {
				newTable1[i++] = key;
			}
		}
		
		for(Object key : table2) {
			if(!isEntryEmpty(key)) {
				newTable1[i++] = key;
			}
		}
		
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
	
	protected int hash1(Object key) {
		return function1.hash(key.hashCode());
	}
	
	protected int hash2(Object key) {
		return function2.hash(key.hashCode());
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<E> iterator() {

		return new Iterator<E>() {

			int it = 0;
			int index1 = 0;
			int index2 = 0;
			
			@Override
			public boolean hasNext() {
				return it < size;
			}

			@SuppressWarnings("unchecked")
			@Override
			public E next() {
				while(index1 < table1.length) {
					if(!isEntryEmpty(table1[index1])) {
						it++;
						return (E) table1[index1++];
					}
					index1++;
				}
				
				while(index2 < table2.length) {
					if(!isEntryEmpty(table2[index2])) {
						it++;
						return (E) table2[index2++];
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
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object key) {
		
		int index = hash1(key);
		if(key.equals(table1[index])) {
			table1[index] = null;
			size--;
			return true;
		}
		
		index = hash2(key);
		if(key.equals(table2[index])) {
			table2[index] = null;
			size--;
			return true;
		}

		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for(E e : c) {
			changed |= add(e);
		}
		
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
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


}
