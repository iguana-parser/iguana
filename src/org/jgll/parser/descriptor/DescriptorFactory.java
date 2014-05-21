package org.jgll.parser.descriptor;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.SPPFNode;


public interface DescriptorFactory {

	public Descriptor createDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, SPPFNode sppfNode);
	
}
