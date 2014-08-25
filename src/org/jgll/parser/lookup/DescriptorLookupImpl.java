package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

public class DescriptorLookupImpl implements DescriptorLookup {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(DescriptorLookupImpl.class);

	private Deque<Descriptor> descriptorsStack;

	public DescriptorLookupImpl(GrammarGraph grammar, Input input) {
		long start = System.nanoTime();
		descriptorsStack = new ArrayDeque<>();
		long end = System.nanoTime();
		log.info("Descriptor lookup initialization: %d ms", (end - start) / 1000_000);
	}
	
	@Override
	public boolean hasNextDescriptor() {
		return !descriptorsStack.isEmpty();
	}

	@Override
	public Descriptor nextDescriptor() {
		return descriptorsStack.pop();
	}
	
	@Override
	public boolean addDescriptor(Descriptor descriptor) {
		return descriptor.getGSSNode().hasDescriptor(descriptor);
	}

	@Override
	public void scheduleDescriptor(Descriptor descriptor) {
		descriptorsStack.push(descriptor);
	}
	
}
