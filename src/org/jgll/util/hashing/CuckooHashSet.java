package org.jgll.util.hashing;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * A hash set based on the Cuckoo hashing algorithm.
 * 
 * @author Ali Afroozeh
 *
 */
public class CuckooHashSet<E> implements Set<E>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 32;
	private static final float DEFAULT_LOAD_FACTOR = 0.4f;
	
	private int initialCapacity;
	
	private int capacity;
	
	private int p;
	
	private int size;
	
	private int threshold;
	
	private float loadFactor;
	
	private int rehashCount;
	
	private int growCount;
	
	private UniversalHashFunction function1;
	
	private UniversalHashFunction function2;
	
	private Object[] table1;

	private Object[] table2;
		
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
		return contains(key, table1, table2);
	}
	
	public boolean contains(Object key, Object[] table1, Object[] table2) {
		int index = hash1(key);
		if(index > table1.length) {
			System.out.println("WTF?");
		}
		if(key.equals(table1[index])) {
			return true;
		}
		
		index = hash2(key);
		if(key.equals(table2[index])) {
			return true;
		}
		
		return false;
	}
	
	private boolean insertAgain(Object key, Object[] table1, Object[] table2) {
		int i = 0;
		while(i < size + 1) {
			i++;

			int index = hash1(key);
			if(table1[index] == null) {
				table1[index] = key;
				return true;
			}
			
			Object tmp = table1[index];
			table1[index] = key;
			key = tmp;
			
			index = hash2(key);
			if(table2[index] == null) {
				table2[index] = key;
				return true;
			}
			
			tmp = table2[index];
			table2[index] = key;
			key = tmp;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean add(E key) {
		
		if(contains(key)) {
			return false;
		}
		
		int i = 0;
		while(i < size + 1) {
			i++;
			
			int index = hash1(key);
			if(table1[index] == null) {
				table1[index] = key;
				size++;
				if(size >= threshold) {
					enlargeTables();
				}
				return true;
			}
			
			E tmp = (E) table1[index];
			table1[index] = key;
			key = tmp;
			
			index = hash2(key);
			if(table2[index] == null) {
				table2[index] = key;
				size++;
				if(size >= threshold) {
					enlargeTables();
				}
				return true;
			}
			
			tmp = (E) table2[index];
			table2[index] = key;
			key = tmp;
		}
		
		rehash();
		return add(key);
	}
	
	private void rehash() {
		Object[] newTable1 = new Object[table1.length];
		Object[] newTable2 = new Object[table2.length];

		generateNewHashFunctions();
		
		for(Object key : table1) {
			if(key != null) {
				// if one element cannot be inserted, restart the whole process with two new
				// hash function.
				if(!insertAgain(key, newTable1, newTable2)) {
					rehash();
					return;
				}
			}
		}
		
		for(Object key : table2) {
			if(key != null) {
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
			if(key != null) {
				newTable1[i++] = key;
			}
		}
		
		for(Object key : table2) {
			if(key != null) {
				newTable1[i++] = key;
			}
		}
		
		table1 = newTable1;
		table2 = newTable2;
		
		growCount++;
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
	
	public int getGrowCount() {
		return growCount;
	}
	
	private int hash1(Object key) {
		return function1.hash(key.hashCode());
	}
	
	private int hash2(Object key) {
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
					if(table1[index1] != null) {
						it++;
						return (E) table1[index1++];
					}
					index1++;
				}
				
				while(index2 < table2.length) {
					if(table2[index2] != null) {
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
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}


}
