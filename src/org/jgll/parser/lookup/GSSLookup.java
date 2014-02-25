package org.jgll.parser.lookup;

import java.util.Map;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface GSSLookup {
	
	public boolean hasNextDescriptor();
	
	public Descriptor nextDescriptor();
	
	public boolean addDescriptor(Descriptor descriptor);
	
	/**
	 * Returns true if there is a GSS edge from source to destination
	 * with the given node as label. If no such edge exists, a new
	 * edge with the given properties will be created.
	 * 
	 */
	public boolean getGSSEdge(GSSNode source, GSSNode destination, SPPFNode node, BodyGrammarSlot returnSlot);

	/**
	 * Returns an already existing GSS node with the given grammar slot and input 
	 * index, or creates a new GSS node with given parameters.  
	 */
	public GSSNode getGSSNode(HeadGrammarSlot head, int inputIndex);
	
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
	
	public int getDescriptorsCount();
	
	public Iterable<GSSNode> getGSSNodes();
	
	public void init(Input input);
	
	public Map<GSSNode, Iterable<GSSEdge>> getEdgesMap();
	
}	
