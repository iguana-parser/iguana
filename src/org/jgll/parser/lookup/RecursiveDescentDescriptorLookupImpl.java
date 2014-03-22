package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgll.grammar.Grammar;
import org.jgll.parser.Descriptor;
import org.jgll.util.Input;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.HashTableFactory;
import org.jgll.util.hashing.IguanaSet;
import org.jgll.util.hashing.hashfunction.HashFunction;
import org.jgll.util.logging.LoggerWrapper;

public class RecursiveDescentDescriptorLookupImpl implements DescriptorLookup {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(RecursiveDescentDescriptorLookupImpl.class);

	private Deque<Descriptor> descriptorsStack;

	private IguanaSet<Descriptor>[] descriptorsSet;
	
	private int tableSize = (int) Math.pow(2, 10);

	private HashTableFactory factory;
	
	@SuppressWarnings("unchecked")
	public RecursiveDescentDescriptorLookupImpl(Grammar grammar, Input input) {
		long start = System.nanoTime();

		descriptorsStack = new ArrayDeque<>();
		descriptorsSet = new IguanaSet[input.length()];

		long end = System.nanoTime();
		log.info("Descriptor lookup initialization: %d ms", (end - start) / 1000_000);

		factory = HashTableFactory.getFactory();
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

		IguanaSet<Descriptor> set = descriptorsSet[descriptor.getInputIndex()];
		if (set == null) {
			set = factory.newHashSet(tableSize, new ExternalHasher<Descriptor>() {

				private static final long serialVersionUID = 1L;

				@Override
				public int hash(Descriptor d, HashFunction f) {
					return f.hash(d.getGrammarSlot().getId(), 
		       					  d.getSPPFNode().getId(), 
								  d.getGSSNode().getGrammarSlot().getId(),
								  d.getGSSNode().getInputIndex());
				}
				
				@Override
				public boolean equals(Descriptor d1, Descriptor d2) {
					return 	d1.getGrammarSlot().getId() == d2.getGrammarSlot().getId() && 
		 					d1.getSPPFNode().getId() == d2.getSPPFNode().getId() && 
							d1.getGSSNode().getGrammarSlot() == d2.getGSSNode().getGrammarSlot() &&
							d1.getGSSNode().getInputIndex() == d2.getGSSNode().getInputIndex();
				}
			});
			descriptorsSet[descriptor.getInputIndex()] = set;
			set.add(descriptor);
			descriptorsStack.push(descriptor);
			log.trace("Descriptor created: %s", descriptor);
			return true;
		}

		Descriptor add = set.add(descriptor);

		if (add == null) {
			descriptorsStack.push(descriptor);
			log.trace("Descriptor created: %s", descriptor);
			return true;
		}

		return false;
	}
	
	@Override
	public int getDescriptorsCount() {
		int count = 0;
		for (int i = 0; i < descriptorsSet.length; i++) {
			if (descriptorsSet[i] != null) {
				count += descriptorsSet[i].size();
			}
		}
		return count;
	}


}
