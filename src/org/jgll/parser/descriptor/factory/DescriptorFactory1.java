package org.jgll.parser.descriptor.factory;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.descriptor.DescriptorFactory;
import org.jgll.parser.descriptor.DescriptorImpl;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.hashfunction.HashFunction;


public class DescriptorFactory1 implements DescriptorFactory {

	@Override
	public Descriptor createDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, SPPFNode sppfNode) {
		return new DescriptorImpl(new ExternalHasher<Descriptor>() {

			private static final long serialVersionUID = 1L;

			@Override
			public int hash(Descriptor descriptor, HashFunction f) {
				return f.hash(descriptor.getGrammarSlot().getId(), descriptor.getSPPFNode().getId(), descriptor.getInputIndex());
			}
			
			@Override
			public boolean equals(Descriptor descriptor1, Descriptor descriptor2) {
				return descriptor1.getGrammarSlot() == descriptor2.getGrammarSlot() &&
					   descriptor1.getSPPFNode().getId() == descriptor2.getSPPFNode().getId() &&
					   descriptor1.getInputIndex() == descriptor2.getInputIndex();

			}
		}, slot, gssNode, inputIndex, sppfNode);
	}

}
