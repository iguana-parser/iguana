package org.jgll.parser;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.jgll.grammar.Grammar;
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

	private final int longestTerminalChain;
	
	@SuppressWarnings("unchecked")
	public LevelledDescritorSet(Grammar grammar, LevelledLookup lookup) {
		this.longestTerminalChain = grammar.getLongestTerminalChain();
		this.lookup = lookup;
		u = new Set[longestTerminalChain + 1];
		r = new Queue[longestTerminalChain + 1];
		
		for(int i = 0; i < longestTerminalChain + 1; i++) {
			u[i] = new HashSet<>();
			r[i] = new ArrayDeque<>();
		}
	}
	
	private int indexFor(int inputIndex) {
		return inputIndex % (longestTerminalChain + 1);
	}
	
	@Override
	public Descriptor nextDescriptor() {
		int index = indexFor(currentLevel); 
		if(!r[index].isEmpty()) {
			size--;
			return r[index].remove();
		} else {
			u[index] = new HashSet<>();			
			currentLevel++;
			lookup.nextLevel();
			return nextDescriptor();
		}
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public void add(Descriptor descriptor) {
	
		int index = indexFor(descriptor.getInputIndex());
		
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
