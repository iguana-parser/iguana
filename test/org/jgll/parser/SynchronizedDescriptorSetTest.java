package org.jgll.parser;

import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.junit.Before;
import org.junit.Test;

public class SynchronizedDescriptorSetTest {
	
	private DescriptorSet descriptorSet = new StackDescriptorSet();
	
	private NonPackedNode sppfNode = new NonterminalSymbolNode(0, 0, 0);
	
	@Before
	public void init() {
		descriptorSet.add(new Descriptor(0, new GSSNode(0, 0), 0, sppfNode));
		descriptorSet.add(new Descriptor(0, new GSSNode(1, 0), 1, sppfNode));
		descriptorSet.add(new Descriptor(0, new GSSNode(2, 0), 2, sppfNode));
		descriptorSet.add(new Descriptor(0, new GSSNode(3, 0), 3, sppfNode));
		descriptorSet.add(new Descriptor(0, new GSSNode(4, 0), 2, sppfNode));
		descriptorSet.add(new Descriptor(0, new GSSNode(5, 0), 2, sppfNode));
	}
	
	@Test
	public void test() {
		System.out.println(descriptorSet.nextDescriptor());
		System.out.println(descriptorSet.nextDescriptor());
		System.out.println(descriptorSet.nextDescriptor());
		System.out.println(descriptorSet.nextDescriptor());
		System.out.println(descriptorSet.nextDescriptor());
		System.out.println(descriptorSet.nextDescriptor());
	}
	
}
