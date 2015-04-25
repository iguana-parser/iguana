package org.iguana.parser.lookup;

import java.util.ArrayDeque;
import java.util.Deque;

import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.util.collections.IntKey2PlusObject;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.util.Input;
import org.iguana.util.collections.IntKey2;
import org.iguana.util.hashing.hashfunction.IntHash2;
import org.iguana.util.hashing.hashfunction.IntHash3;

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
		return gssNode.hasDescriptor(IntKey2PlusObject.from(env, slot.getId(), inputIndex, f3));
	}
	
}
