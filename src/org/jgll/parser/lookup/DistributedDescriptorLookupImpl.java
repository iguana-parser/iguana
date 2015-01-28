package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgll.datadependent.env.Environment;
import org.jgll.datadependent.util.collections.IntKey2PlusEnv;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;
import org.jgll.util.collections.IntKey2;
import org.jgll.util.hashing.hashfunction.IntHash2;
import org.jgll.util.hashing.hashfunction.IntHash3;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class DistributedDescriptorLookupImpl implements DescriptorLookup {

	private Deque<Descriptor> descriptorsStack;
	private IntHash2 f;
	private IntHash3 f3;

	public DistributedDescriptorLookupImpl(Input input, Grammar grammar) {
		int inputSize = input.length() + 1;
		this.f = (x, y) -> x * inputSize + y;
		this.f3 = (x, y, z) -> x * inputSize * inputSize + y * inputSize + z;
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

	@Override
	public boolean addDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode, Environment env) {
		return gssNode.hasDescriptor(IntKey2PlusEnv.from(slot.getId(), inputIndex, env, f3));
	}
	
}
