package org.jgll.disambiguation;

import java.util.List;

import org.jgll.exception.DisambiguationFailedException;
import org.jgll.sppf.SPPFNode;

public interface DisambiguationRule {
	

	/**
	 * Applies the disambiguation rule on the given SPPF node. A disambiguation rule
	 * should remove a single packed node containing an illegal pattern, or remove a
	 * packed node while a preferred packed node is present.
	 *  
	 * 
	 * @param node an ambiguous SPPF node
	 * 
	 * @throws DisambiguationFailedException if the rule cannot be applied on this node
	 */
	public void apply(SPPFNode node);
	
	/**
	 * Returns the labels of the ambiguity nodes under which 
	 * this rule can be applied.
	 * Implementations should return an immutable, non-null lis of
	 * Strings.  
	 * 
	 * @return An empty list if no parent is specified. In this case, the rule
	 * 			   will be applied on all ambiguity nodes.
	 */
	public List<String> getRootNode();
	
	
	/**
	 * 
	 * Returns the name of this rule
	 * 
	 * @return null if no name is assigned to this rule.
	 */
	public String getRuleName();
	
}
