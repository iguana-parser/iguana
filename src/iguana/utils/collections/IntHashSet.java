package iguana.utils.collections;


import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class IntHashSet implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	private static final float DEFAULT_LOAD_FACTOR = 0.7f;
	
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
	
	private int[] table;
	
 	@SafeVarargs
	public static IntHashSet from(int...elements) {
 		IntHashSet set = new IntHashSet();
		for(int e : elements) {
			set.add(e);
		}
		return set;
	}

	public IntHashSet() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public IntHashSet(int initalCapacity) {
		this(initalCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	public IntHashSet(int initialCapacity, float loadFactor) {
		
		this.initialCapacity = initialCapacity;
		
		this.loadFactor = loadFactor;

		capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        
		bitMask = capacity - 1;
		
		threshold = (int) (loadFactor * capacity);
		table = new int[capacity];
		Arrays.fill(table, -1);
	}
	
	public boolean contains(int key) {
		return get(key) != -1;
	}
	
	public int add(int key) {
		
		int index = hash(key);

		do {
			if(table[index] == -1) {
				table[index] = key;
				size++;
				if (size >= threshold) {
					rehash();
				}
				return -1;
			}
			
			else if(table[index] == key) {
				return table[index];
			}
			
			collisionsCount++;
			
			index = (index + 1) & bitMask;
			
		} while(true);
	}
	
	private void rehash() {
		
		capacity <<= 1;
		
		bitMask = capacity - 1;
		
		int[] newTable = new int[capacity];
		Arrays.fill(newTable, -1);
		
		label:
		for(int key : table) {
			if(key != -1) {
				
				int index = hash(key);

				do {
					if(newTable[index] == -1) {
						newTable[index] = key;
						continue label;
					}
					
					index = (index + 1) & bitMask;
					
				} while(true);
			}
		}
		
		table = newTable;
		
		threshold = (int) (loadFactor * capacity);
		rehashCount++;
	}
	
	private int hash(int key) {
		return key * 31 & bitMask;
	}

	public int get(int key) {
		return table[hash(key)];
	}

	public int size() {
		return size;
	}

	public int getInitialCapacity() {
		return initialCapacity;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public int getEnlargeCount() {
		return rehashCount;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void clear() {
		for(int i = 0; i < table.length; i++) {
			table[i] = -1;
		}
		size = 0;
	}
	
	public int getCollisionCount() {
		return collisionsCount++;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for(int t : table)
			if(t != -1)
				sb.append(t).append(", ");

		if(sb.length() > 2)
			sb.delete(sb.length() - 2, sb.length());

		sb.append("}");
		return sb.toString();
	}
}
