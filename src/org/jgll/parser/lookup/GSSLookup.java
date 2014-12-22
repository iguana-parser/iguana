package org.jgll.parser.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
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
	
	public Iterable<GSSNode> getGSSNodes();
}	
