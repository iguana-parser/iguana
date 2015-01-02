package org.jgll.util.hashing;

import java.io.Serializable;
import java.util.Iterator;

import org.jgll.parser.HashFunctions;
import org.jgll.util.RandomUtil;
import org.jgll.util.hashing.hashfunction.HashFunction;

/**
 * 
 * A hash set based on Cuckoo hashing.
 * This implementation uses separate arrays for keeping values, instead
 * of being backed by a hash set.
 *
 * 
 * @author Ali Afroozeh
 *
 */
public class CuckooHashMap2<K, V> implements IguanaMap<K, V>, Serializable {
	
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
	
	protected K[] table1;
	
	protected V[] values1;

	protected K[] table2;
	
	protected V[] values2;

	protected int tableSize;
	
	private ExternalHashEquals<K> externalHasher;
	
	private int collisionsCount;
		
	public CuckooHashMap2(ExternalHashEquals<K> hasher) {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, hasher);
	}
	
	public CuckooHashMap2(int initalCapacity, ExternalHashEquals<K> hasher) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR, hasher);
	}
	
	@SuppressWarnings("unchecked")
	public CuckooHashMap2(int initialCapacity, float loadFactor, ExternalHashEquals<K> hasher) {
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
		table1 = (K[]) new Object[tableSize];
		values1 = (V[]) new Object[tableSize];
		table2 = (K[]) new Object[tableSize];
		values2 = (V[]) new Object[tableSize];
		
		generateNewHashFunctions();
	}
	
	private void generateNewHashFunctions() {
		function1 = HashFunctions.murmur3(RandomUtil.random.nextInt(Integer.MAX_VALUE));
		function2 = HashFunctions.murmur3(RandomUtil.random.nextInt(Integer.MAX_VALUE));
	}
	
	@Override
	public boolean containsKey(K key) {
		return get(key) != null;
	}
	
	/**
	 * 
	 * Returns the value associated with the element stored in the map.
	 * 
	 * @param k
	 * 
	 * @return null if no element matching the given key is found.
	 */
	@Override
	public V get(K k) {
	
		int index = indexFor(externalHasher.hash(k, function1));
		
		K key = table1[index];
		if(key != null && externalHasher.equals(k, key)) {
			return values1[index];
		}			
		
		index = indexFor(externalHasher.hash(k, function2));
		key = table2[index];
		if(key != null && externalHasher.equals(k, key)) {
			return values2[index];
		}

		return null;
	}
	
	/**
	 * Returns the associated value with the given k. If there is already 
	 * an association with the given key, the old value is replaced by 
	 * the new value and the old value is returned.
	 *  
	 * @return null if there is no existing (k, v) association in the map,
	 * 			    otherwise it returns the old value.
	 */
	private V get(K k, V v) {
		
		int index = indexFor(externalHasher.hash(k, function1));
		
		K key = table1[index];
		if(key != null && externalHasher.equals(k, key)) {
			V old = values1[index];
			values1[index] = v;
			return old;
		}			
		
		index = indexFor(externalHasher.hash(k, function2));
		key = table2[index];
		if(key != null && externalHasher.equals(k, key)) {
			V old = values2[index];
			values2[index] = v;
			return old;
		}

		return null;
	}
	
	
	/**
	 * 
	 * Adds the given key to the set if it does not already exist in the set.
	 * 
	 * @param k 
	 * @return A reference to the old key stored in the set if the key was in 
	 *         the set, otherwise returns null. 
	 */
	@Override
	public V put(K k, V v) {
		
		if(size >= threshold) {
			enlargeTables();
		}
		
		V value = get(k, v);
		
		if(value != null) {
			return value;
		}
		
		k = tryInsert(k, v);

		while(k != null) {
			generateNewHashFunctions();
			rehash();
			k = tryInsert(k, v);
		}

		size++;
		return null;
	}
		
	private K tryInsert(K k, V v) {
		int i = 0;
		while(i < size + 1) {
			i++;
			
			k = insert(k, v);
			if(k == null) {
				return null;
			}
		}
		
		return k;
	}
	
	/**
	 * 
	 * Inserts the key into the first table. If the intended cell is full,
	 * the current entry is replaced by the new entry and and old one is 
	 * tried to be placed in the second table.   
	 * 
	 * @param k
	 * 
	 * @return null if an empty spot could be found in the first or the second table.
	 *              Otherwise, the existing key in the second table is returned.
	 */
	private K insert(K k, V v) {
		int index = indexFor(externalHasher.hash(k, function1));
		if(table1[index] == null) {
			table1[index] = k;
			values1[index] = v;
			return null;
		}
		
		collisionsCount++;
		
		K tmpKey = table1[index];
		V tmpVal = values1[index];
		
		table1[index] = k;
		values1[index] = v;
		k = tmpKey;
		v = tmpVal;
		
		index = indexFor(externalHasher.hash(k, function2));
		if(table2[index] == null) {
			table2[index] = k;
			values2[index] = v;
			return null;
		}
		
		collisionsCount++;
		
		tmpKey = table2[index];
		tmpVal = values2[index];
		table2[index] = k;
		values2[index] = v;
		k = tmpKey;
		v = tmpVal;
		
		return k;
	}
	
	private void rehash() {
		
		rehashCount++;

		table1Loop:
		for(int i = 0; i < table1.length; i++) {
			K key = table1[i];
			if(key != null) {
				if(indexFor(externalHasher.hash(key, function1)) != i) {
					K tmpKey = table1[i];
					V tmpVal = values1[i];
					table1[i] = null;

					tmpKey = tryInsert(tmpKey, tmpVal);
					if(tmpKey == null) {
						continue table1Loop;
					}
					
					// If the control flow reaches here, it means that the insertion in 
					// the rehash has failed, and we need a new set of hash functions.
					// We put the tmp back into an empty spot in the table and start again.
					putInEmptySlot(tmpKey);
					
					generateNewHashFunctions();
					rehash();
					return;
				}
			}
		}
	
		table2Loop:
		for(int i = 0; i < table2.length; i++) {
			K key = table2[i];
			if(key != null) {
				if(indexFor(externalHasher.hash(key, function2)) != i) {
					K tmp = table2[i];
					V tmpVal = values2[i];
					table2[i] = null;
	
					tmp = tryInsert(tmp, tmpVal);
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
	
	private void putInEmptySlot(K key) {
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
		
		K[] newTable1 = (K[]) new Object[capacity];
		V[] newValues1 = (V[]) new Object[capacity];
		K[] newTable2 = (K[]) new Object[capacity];
		V[] newValues2 = (V[]) new Object[capacity];
		
		tableSize = capacity;
		capacity *= 2;
		
		threshold = (int) (loadFactor * capacity);
		
		System.arraycopy(table1, 0, newTable1, 0, table1.length);
		System.arraycopy(table2, 0, newTable2, 0, table2.length);
		System.arraycopy(values1, 0, newValues1, 0, values1.length);
		System.arraycopy(values2, 0, newValues2, 0, values2.length);
		
		table1 = newTable1;
		table2 = newTable2;
		values1 = newValues1;
		values2 = newValues2;
		
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
	
	@Override
	public int getRehashCount() {
		return rehashCount;
	}

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

	public Iterator<K> keyIterator() {

		return new Iterator<K>() {

			int it = 0;
			int index1 = 0;
			int index2 = 0;
			
			@Override
			public boolean hasNext() {
				return it < size;
			}

			@Override
			public K next() {
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
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for(K t : table1) {
			if(t != null) 
				sb.append(t).append(", ");
		}
		for(K t : table2) {
			if(t != null)
				sb.append(t).append(", ");
		}
		
		if(sb.length() > 2)
			sb.delete(sb.length() - 2, sb.length());
		
		sb.append("}");
		return sb.toString();
	}

	public int getCollisionCount() {
		return collisionsCount;
	}

}