package org.jgll.parser.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

public interface DescriptorLookup {

	public boolean hasNextDescriptor();
	
	public Descriptor nextDescriptor();
	
	public void scheduleDescriptor(Descriptor descriptor);
	
	public boolean addDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode);

}
