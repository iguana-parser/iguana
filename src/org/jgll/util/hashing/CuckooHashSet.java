package org.jgll.util.hashing;

import java.util.Random;

/**
 * 
 * A hash set based on the chuckoo's hashing algorithm.
 * 
 * @author Ali Afroozeh
 *
 */
public class CuckooHashSet {
	
	private static final int DEFAULT_INITIAL_CAPACITY = 32;
	private static final float DEFAULT_LOAD_FACTOR = 0.49f;
	
	private int initialCapacity;
	
	private int capacity;
	
	private int p;
	
	private int size;
	
	private int threshold;
	
	private float loadFactor;
	
	private int rehashCount;
	
	private int growCount;
	
	private HashFunction hashFunction;
	
	private int a1, a2, b1, b2;
		
	private int bitMask;
	
	private HashKey[] table1;

	private HashKey[] table2;
	
	private int maxA;

	private int maxB;
	
	
	public CuckooHashSet() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public CuckooHashSet(int initalCapacity) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	public CuckooHashSet(int initialCapacity, float loadFactor) {
		this.initialCapacity = initialCapacity;
		this.loadFactor = loadFactor;

		capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
            p++;
        }
        
        // For each table
		bitMask = (capacity >> 1) - 1;
		p--;
		
		threshold = (int) (loadFactor * capacity);
		
		table1 = new HashKey[capacity >> 1];
		table2 = new HashKey[capacity >> 1];
		
		hashFunction = new MurmurHash3();
		
		maxB = 1 << (32 - p);
		
		maxA = Integer.MAX_VALUE >> 1;
		
		generateNewHashFunctions();
	}

	private void generateNewHashFunctions() {
		Random rand = new Random();
		
		a1 = 2 * rand.nextInt(maxA) + 1;
		b1 = rand.nextInt(maxB);
		a2 = 2 * rand.nextInt(maxA) + 1;
		b2 = rand.nextInt(maxB);
	}
	
	public boolean contains(HashKey key) {
		return contains(key, table1, table2);
	}
	
	public boolean contains(HashKey key, HashKey[] table1, HashKey[] table2) {
		int index = hash1(key);
		if(key.equals(table1[index])) {
			return true;
		}
		
		index = hash2(key);
		if(key.equals(table2[index])) {
			return true;
		}
		
		return false;
	}
	
	private boolean insertAgain(HashKey key, HashKey[] table1, HashKey[] table2) {
		int i = 0;
		while(i < size + 1) {
			i++;

			int index = hash1(key);
			if(table1[index] == null) {
				table1[index] = key;
				return true;
			}
			
			HashKey tmp = table1[index];
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
	
	public void add(HashKey key) {
		
		if(contains(key)) {
			return;
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
				return;
			}
			
			HashKey tmp = table1[index];
			table1[index] = key;
			key = tmp;
			
			index = hash2(key);
			if(table2[index] == null) {
				table2[index] = key;
				size++;
				if(size >= threshold) {
					enlargeTables();
				}
				return;
			}
			
			tmp = table2[index];
			table2[index] = key;
			key = tmp;
		}
		rehash();
		add(key);
	}
	
	private void rehash() {
		HashKey[] newTable1 = new HashKey[table1.length];
		HashKey[] newTable2 = new HashKey[table2.length];

		generateNewHashFunctions();
		
		for(HashKey key : table1) {
			if(key != null) {
				// if one element cannot be inserted, restart the whole process with two new
				// hash function.
				if(!insertAgain(key, newTable1, newTable2)) {
					rehash();
					return;
				}
			}
		}
		
		for(HashKey key : table2) {
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
		capacity <<= 1;
		p++;
		threshold = (int) (loadFactor * capacity);
		
		bitMask = (capacity >> 1) - 1;
		
		HashKey[] newTable1 = new HashKey[capacity >> 1];
		HashKey[] newTable2 = new HashKey[capacity >> 1];

		int i = 0;
		for(HashKey key : table1) {
			if(key != null) {
				newTable1[i++] = key;
			}
		}
		
		for(HashKey key : table2) {
			if(key != null) {
				newTable1[i++] = key;
			}
		}
		
		table1 = newTable1;
		table2 = newTable2;
		
		threshold = (int) (loadFactor * capacity);
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
	
	private int hash1(HashKey key) {
		return (a1 * key.hash(hashFunction) + b1) >>> (32 - p);
	}
	
	private int hash2(HashKey key) {
		return (a2 * key.hash(hashFunction) + b2) >>> (32 - p);
	}


}
