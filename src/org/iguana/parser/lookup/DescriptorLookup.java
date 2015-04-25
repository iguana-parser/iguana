package org.iguana.parser.lookup;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;

public interface DescriptorLookup {

	public boolean hasNextDescriptor();
	
	public Descriptor nextDescriptor();
	
	public void scheduleDescriptor(Descriptor descriptor);
	
	public boolean addDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode);
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	public boolean addDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode, Environment env);

}
