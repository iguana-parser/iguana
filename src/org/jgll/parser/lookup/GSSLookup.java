package org.jgll.parser.lookup;

import java.util.Map;

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
	public GSSNode getGSSNode(GrammarSlot head, int inputIndex);
	
	/**
	 * Returns an already existing GSS node with the given parametrs. If no such
	 * GSS node exists, returns null.
	 */
	public GSSNode hasGSSNode(GrammarSlot head, int inputIndex);
	
	/**
	 * Returns the GSS nodes reachable from the given GSS node.
	 * 
	 */
	public Iterable<GSSNode> getChildren(GSSNode node);
	
	public Iterable<GSSEdge> getEdges(GSSNode node);

	public void addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode);
	
	public Iterable<NonPackedNode> getPoppedElementsOf(GSSNode gssNode);
	
	public int getGSSNodesCount();
	
	public int getGSSEdgesCount();
	
	public Iterable<GSSNode> getGSSNodes();
	
	public Map<GSSNode, Iterable<GSSEdge>> getEdgesMap();
	
}	
