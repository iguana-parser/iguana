package org.jgll.parser;

/**
 * DescriptorSet defines the interface of the set R 
 * in the GLL parsing algorithm. The set R maintains the
 * list of descriptors which should be processed.
 * 
 * Each descriptor should be added to R only once. In the algorithm
 * a separate set (sets) is used to maintain the uniqueness. However,
 * the implementation of the <code>DescriptorSet</code> interface 
 * should maintain a history of added elements and do not add an element
 * if it's already added.
 * 
 * @author Ali Afroozeh
 *
 */
public interface DescriptorSet {

	/**
	 * Returns the next descriptor 
	 * 
	 * @throws IllegalStateException if there is no descriptor left
	 */
	public Descriptor nextDescriptor();
	
	/**
	 * Returns true if there are more elements in the descriptor set
	 * to be proccessed. 
	 */
	public boolean isEmpty();
	
	/**
	 * Adds a descriptor to the set of descriptors if it is not already
	 * added.
	 */
	public void add(Descriptor descriptor);
	
	/**
	 * Removes all the elements from this descriptor set.
	 */
	public void clear();
	
	/**
	 * Returns the number of all unique descritpors added to
	 * the descriptor set.  
	 */
	public int sizeAll();
}
