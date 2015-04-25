package org.iguana.parser.gss.lookup;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.gss.GSSEdge;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.sppf.NonPackedNode;
/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface GSSLookup {
	
	/**
	 * Returns an already existing GSS node with the given grammar slot and input 
	 * index, or creates a new GSS node with given parameters.  
	 */
	public GSSNode getGSSNode(GrammarSlot slot, int inputIndex);
	
	/**
	 * Returns an already existing GSS node with the given parametrs. If no such
	 * GSS node exists, returns null.
	 */
	public GSSNode hasGSSNode(GrammarSlot slot, int inputIndex);
	
	public boolean getGSSEdge(GSSNode gssNode, GSSEdge edge);

	public boolean addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode);
	
	public int getGSSNodesCount();
	
	public int getGSSEdgesCount();
	
	public void reset();
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	public <T> GSSNode getGSSNode(GrammarSlot slot, int inputIndex, GSSNodeData<T> data);
	
	public <T> GSSNode hasGSSNode(GrammarSlot slot, int inputIndex, GSSNodeData<T> data);
	
}	
