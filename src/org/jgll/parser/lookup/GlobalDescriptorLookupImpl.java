package org.jgll.parser.lookup;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.jgll.datadependent.env.Environment;
import org.jgll.datadependent.util.collections.IntKey4PlusEnv;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.collections.IntKey4;
import org.jgll.util.collections.Key;
import org.jgll.util.hashing.hashfunction.HashFunction;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GlobalDescriptorLookupImpl implements DescriptorLookup {

	private final Set<Key> set;
	private final Deque<Descriptor> descriptorsStack;
	private final HashFunction f;
	
	public GlobalDescriptorLookupImpl(HashFunction f) {
		this.f = f;
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

	@Override
	public boolean addDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode, Environment env) {
		return !set.add(IntKey4PlusEnv.from(slot.getId(), inputIndex, gssNode.getGrammarSlot().getId(), gssNode.getInputIndex(), env, f));
	}
	
}
