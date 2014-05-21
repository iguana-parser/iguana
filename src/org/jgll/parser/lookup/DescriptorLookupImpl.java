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

	private int descriptorsCount;
	
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
		return descriptor.getGSSNode().addDescriptor(descriptor);
//		IguanaSet<Descriptor> set = descriptorsSet[descriptor.getInputIndex()];
//		if (set == null) {
//			set = factory.newHashSet(tableSize, new ExternalHasher<Descriptor>() {
//
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public int hash(Descriptor d, HashFunction f) {
//					return f.hash(d.getGrammarSlot().getId(), 
//		       					  d.getSPPFNode().getId(), 
//								  d.getGSSNode().getGrammarSlot().getId(),
//								  d.getGSSNode().getInputIndex());
//				}
//				
//				@Override
//				public boolean equals(Descriptor d1, Descriptor d2) {
//					return 	d1.getGrammarSlot().getId() == d2.getGrammarSlot().getId() && 
//		 					d1.getSPPFNode().getId() == d2.getSPPFNode().getId() && 
//							d1.getGSSNode().getGrammarSlot() == d2.getGSSNode().getGrammarSlot() &&
//							d1.getGSSNode().getInputIndex() == d2.getGSSNode().getInputIndex();
//				}
//			});
//			descriptorsSet[descriptor.getInputIndex()] = set;
//			set.add(descriptor);
//			return true;
//		}
//
//		Descriptor add = set.add(descriptor);
//		return add == null;
	}

	@Override
	public boolean scheduleDescriptor(Descriptor descriptor) {

		if (addDescriptor(descriptor)) {
			descriptorsCount++;
			descriptorsStack.push(descriptor);
			log.trace("Descriptor created: %s", descriptor);
			return true;
		}

		return false;
	}
	
	@Override
	public int getDescriptorsCount() {
		return descriptorsCount;
	}


}
