package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.collections.IntKey4;
import org.jgll.util.hashing.hashfunction.IntHash4;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GlobalDescriptorLookupImpl implements DescriptorLookup {

	private final Set<IntKey4> set;
	private final Deque<Descriptor> descriptorsStack;
	private final IntHash4 f;
	
	public GlobalDescriptorLookupImpl(int inputSize, int grammarSize) {
		this.f = (x, y, z, w) -> x * inputSize * grammarSize * inputSize +
								 y * grammarSize * inputSize +
								 z * inputSize +
								 w;
		set = new HashSet<>();
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
		return !set.add(IntKey4.from(slot.getId(), inputIndex, gssNode.getGrammarSlot().getId(), gssNode.getInputIndex(), f));
	}

	@Override
	public void scheduleDescriptor(Descriptor descriptor) {
		descriptorsStack.push(descriptor);
	}
	
}
