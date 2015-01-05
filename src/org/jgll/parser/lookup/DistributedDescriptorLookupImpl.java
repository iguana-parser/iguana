package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.collections.IntKey2;
import org.jgll.util.hashing.hashfunction.HashFunction;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class DistributedDescriptorLookupImpl implements DescriptorLookup {

	private Deque<Descriptor> descriptorsStack;
	private HashFunction f;

	public DistributedDescriptorLookupImpl(HashFunction f) {
		this.f = f;
		descriptorsStack = new ArrayDeque<>();
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
	public boolean addDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode) {
		return gssNode.hasDescriptor(IntKey2.from(slot.getId(), inputIndex, f));
	}

	@Override
	public void scheduleDescriptor(Descriptor descriptor) {
		descriptorsStack.push(descriptor);
	}
	
}
