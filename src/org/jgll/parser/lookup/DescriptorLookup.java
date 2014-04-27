package org.jgll.parser.lookup;

import org.jgll.parser.Descriptor;

public interface DescriptorLookup {

	public boolean hasNextDescriptor();
	
	public Descriptor nextDescriptor();
	
	public boolean scheduleDescriptor(Descriptor descriptor);
	
	public boolean addDescriptor(Descriptor descriptor);

	public int getDescriptorsCount();
	
}
