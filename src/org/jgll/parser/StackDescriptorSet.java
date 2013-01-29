package org.jgll.parser;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * 
 * @author Ali Afroozeh
 * 
 */
public final class StackDescriptorSet implements DescriptorSet {

	private Stack<Descriptor> stack;
	
	private Set<Descriptor> set;
	
	public StackDescriptorSet() {
		stack = new Stack<Descriptor>();
		set = new HashSet<Descriptor>(1024); 
	}
	
	@Override
	public Descriptor nextDescriptor() {
		
		if(isEmpty()) {
			throw new IllegalArgumentException("The descriptor set is empty.");
		}
		
		return stack.pop();
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public void add(Descriptor descriptor) {
		if(set.contains(descriptor)) {
			return;
		}

		stack.push(descriptor);
		set.add(descriptor);
	}

	@Override
	public void clear() {
		stack.clear();
		set.clear();
	}

	@Override
	public int sizeAll() {
		return set.size();
	}

}
