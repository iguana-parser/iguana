package org.jgll.parser;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.jgll.lookup.LevelledLookup;
//import org.jgll.util.OpenAddressingHashSet;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class LevelledDescritorSet implements DescriptorSet {

	private Set<Descriptor>[] u;
	
	private Queue<Descriptor>[] r;
	
	/**
	 * The number of descriptors waiting to be processed.
	 */
	private int size;
	
	/**
	 * The total number of descriptors added
	 */
	private int all;
	
	/**
	 * The current input level being processed
	 */
	private int currentLevel;

	private final LevelledLookup lookup;
	
	@SuppressWarnings("unchecked")
	public LevelledDescritorSet(int inputSize, LevelledLookup lookup) {
		this.lookup = lookup;
		u = new Set[inputSize];
		r = new Queue[inputSize];
	}
	
	@Override
	public Descriptor nextDescriptor() {
		if(r[currentLevel].isEmpty()) {
//			System.out.println(u[currentLevel].getRehashCount());
//			System.out.println("size: " + u[currentLevel].size());
			u[currentLevel] = null;
			r[currentLevel] = null;
			
			
			currentLevel++;
			while(r[currentLevel] == null) {
				currentLevel++;
			}
			
			lookup.nextLevel();
		}
		size--;
		return r[currentLevel].remove(); 
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public void add(Descriptor descriptor) {
	
		int index = descriptor.getInputIndex();
		
		// if no descriptor with the given index is added before
		if(r[index] == null) {
			r[index] = new ArrayDeque<Descriptor>();
			u[index] =  new HashSet<>();
			r[index].add(descriptor);
			u[index].add(descriptor);
			size++;
			all++;
			return;
		}
		
		if(! u[index].contains(descriptor)) {
			 r[index].add(descriptor);
			 u[index].add(descriptor);
			 size++;
			 all++;
		}
	}

	@Override
	public void clear() {
		u[currentLevel] =  null;		
		r[currentLevel] = null;
	}
	
	int getSize() {
		return size;
	}

	@Override
	public int sizeAll() {
		return all;
	}

}
