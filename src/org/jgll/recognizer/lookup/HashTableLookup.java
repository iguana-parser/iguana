package org.jgll.recognizer.lookup;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.recognizer.Descriptor;
import org.jgll.recognizer.GSSNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.logging.LoggerWrapper;


public class HashTableLookup implements Lookup {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(HashTableLookup.class);
	
	protected CuckooHashSet<Descriptor> descriptorSet;
	
	protected Deque<Descriptor> descriptorStack;
	
	protected CuckooHashSet<GSSNode> gssNodes;

	public HashTableLookup() {
		descriptorSet = new CuckooHashSet<>(Descriptor.externalHasher);
		descriptorStack = new ArrayDeque<>();
		gssNodes = new CuckooHashSet<>(GSSNode.externalHasher);
	}
	
	@Override
	public boolean hasDescriptor() {
		return !descriptorStack.isEmpty();
	}
	
	@Override
	public Descriptor nextDescriptor() {
		return descriptorStack.pop();
	}

	@Override
	public void init(Input input) {
		descriptorSet.clear();
		descriptorStack.clear();
		gssNodes.clear();
	}

	@Override
	public boolean addDescriptor(Descriptor descriptor) {
		if(descriptorSet.add(descriptor) == null) {
			log.trace("Descriptor added: %s : true", descriptor);
			descriptorStack.push(descriptor);
			return true;
		} else {
			log.trace("Descriptor %s : false", descriptor);
			return false;
		}
	}

	@Override
	public boolean hasGSSEdge(GSSNode source, GSSNode destination) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GSSNode getGSSNode(GrammarSlot slot, int inputIndex) {
		GSSNode key = new GSSNode(slot, inputIndex);
		GSSNode v = gssNodes.add(key);

		if(v == null) {
			log.trace("GSSNode created: (%s, %d)",  slot, inputIndex);
			v = key;
		}
		
		return v;
	}

	@Override
	public int getGSSNodesCount() {
		return gssNodes.size();
	}

	@Override
	public int getGSSEdgesCount() {
		return 0;
	}

	@Override
	public int getDescriptorsCount() {
		return descriptorSet.size();
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		return gssNodes;
	}

}
