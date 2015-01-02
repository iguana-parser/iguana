package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgll.parser.descriptor.Descriptor;
import org.jgll.util.logging.LoggerWrapper;

public class DistributedDescriptorLookupImpl implements DescriptorLookup {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(DistributedDescriptorLookupImpl.class);

	private Deque<Descriptor> descriptorsStack;

	public DistributedDescriptorLookupImpl() {
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
