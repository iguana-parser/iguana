package org.jgll.parser.lookup;

import org.jgll.parser.descriptor.Descriptor;

public interface DescriptorLookup {

	public boolean hasNextDescriptor();
	
	public Descriptor nextDescriptor();
	
	public void scheduleDescriptor(Descriptor descriptor);
	
	public boolean addDescriptor(Descriptor descriptor);

}
