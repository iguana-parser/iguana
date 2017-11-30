package iguana.utils.collections;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChainingIntHashMap<T> implements IntHashMap<T>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	private int initialCapacity;
	
	private int capacity;
	
	private int size;
	
	private int threshold;
	
	private float loadFactor;
	
	private int rehashCount;
	
	private int collisionsCount;
		
	/**
	 * capacity - 1
	 * The bitMask is used to adj the p most-significant bytes of the multiplicaiton.
	 */
	private int bitMask;
	
	private Entry<T>[] table;
	
	public ChainingIntHashMap() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public ChainingIntHashMap(int initalCapacity) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	@SuppressWarnings("unchecked")
	public ChainingIntHashMap(int initialCapacity, float loadFactor) {
		
		this.initialCapacity = initialCapacity;
		
		this.loadFactor = loadFactor;

		capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        
		bitMask = capacity - 1;
		
		threshold = (int) (loadFactor * capacity);
		table = new Entry[capacity];
	}
	
	private int hash(int key) {
		return key & bitMask;
	}
	
	@Override
	public boolean containsKey(int key) {
		return get(key) != null;
	}
	
	@Override
	public T computeIfAbsent(int key, IntFunction<T> f) {
		int index = hash(key);
		
		Entry<T> entry = table[index];
		
		if (entry == null) {
			entry = new Entry<>(key, f.apply(key));
			table[index] = entry;
			size++;
			if (size >= threshold) rehash();
			return null;
		} 
		else if (entry.key == key) {
			T oldVal = entry.val;
			entry.val = f.apply(key);
			return oldVal;
		} 
		else {
			
			while(entry.next != null) entry = entry.next;
			
			Entry<T> newEntry = new Entry<>(key, f.apply(key));
			entry.next = newEntry;
			size++;
			return null;
		}
	}
	
	@Override
	public T compute(int key, IntKeyMapper<T> f) {
		int index = hash(key);
		
		Entry<T> entry = table[index];
		
		if (entry == null) {
			entry = new Entry<>(key, f.apply(key, null));
			table[index] = entry;
			size++;
			if (size >= threshold) rehash();
			return null;
		} 
		else if (entry.key == key) {
			T oldVal = entry.val;
			entry.val = f.apply(key, oldVal);
			return oldVal;
		} 
		else {
			
			while (entry.next != null) entry = entry.next;
			
			Entry<T> newEntry = new Entry<>(key, f.apply(key, null));
			entry.next = newEntry;
			size++;
			return null;
		}
	}
	
	@Override
	public T put(int key, T value) {
		int index = hash(key);
		
		Entry<T> entry = table[index];
		
		if (entry == null) {
			entry = new Entry<>(key, value);
			table[index] = entry;
			size++;
			if (size >= threshold) rehash();
			return null;
		} 
		else if (entry.key == key) {
			T oldVal = entry.val;
			entry.val = value;
			return oldVal;
		} 
		else {
			
			while (entry.next != null) entry = entry.next;
			
			Entry<T> newEntry = new Entry<>(key, value);
			entry.next = newEntry;
			size++;
			return null;
		}
	}
	
	@Override
	public T remove(int key) {
		throw new UnsupportedOperationException();
	}
	
	private void rehash() {
		
		capacity <<= 1;
		
		bitMask = capacity - 1;
		
		@SuppressWarnings("unchecked")
		Entry<T>[] newTable = new Entry[capacity];
		
		for (Entry<T> e : this) {
			int index = hash(e.key);
			Entry<T> entry = newTable[index];

			if (entry == null) {
				newTable[index] = new Entry<>(e.key, e.val);
				continue;
			} else {
				while (entry.next != null) entry = entry.next;
				
				Entry<T> newEntry = new Entry<>(e.key, e.val);
				entry.next = newEntry;
				entry = newEntry;
			}
		}
		
		table = newTable;
		
		threshold = (int) (loadFactor * capacity);
		rehashCount++;
	}
		
	@Override
	public T get(int key) {
		int index = hash(key);
		Entry<T> entry = table[index];
		
		while (true) {
			if (entry.val == null) 
				return null;
			else if (entry.key == key)
				return entry.val;
			else 
				entry = entry.next;
		}
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
	public int getEnlargeCount() {
		return rehashCount;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public void clear() {
		Arrays.fill(table, -1);
		Arrays.fill(table, null);
		size = 0;
	}

	public int getCollisionCount() {
		return collisionsCount++;
	}
	
	public String toString() {
		return "{" + StreamSupport.stream(this.spliterator(), false)
								  .map(e -> e.key + "=" + e.val)
								  .collect(Collectors.joining(", "))+ "}";
	}
	
	@Override
	public Iterator<Entry<T>> iterator() {
		return new Iterator<Entry<T>>() {
			
			int i = 0;
			Entry<T> current;
			
			@Override
			public boolean hasNext() {
				if (current == null) {
					current = nextInTable();
					return current == null ? false : true;
				}
				
				current = nextInChain();
				if (current == null) {
					i++;
					current = nextInTable();
					if (current == null)
						return false;
				}
			
				return true;
			}
			
			private Entry<T> nextInChain() {
				if (current.next == null) return null;
				return current = current.next;
			}
			
			private Entry<T> nextInTable() {
				if (i == table.length) return null;
				while ((current = table[i]) == null) { 
					i++;
					if (i == table.length) {
						current = null;
						break;
					}
				}
				return current;
			}

			@Override
			public Entry<T> next() {
				return current;
			}
		};
	}

	@Override
	public Iterable<T> values() {
		return null;
	}
	
}
