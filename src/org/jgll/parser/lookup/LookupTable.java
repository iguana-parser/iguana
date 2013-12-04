package org.jgll.parser.lookup;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GSSEdge;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface LookupTable {
	
	public boolean hasNextDescriptor();
	
	public Descriptor nextDescriptor();
	
	public boolean addDescriptor(Descriptor descriptor);
	
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent);
	
	/**
	 * 
	 * Returns an existing SPPF node with the given parameters. If such a node
	 * does not exists, creates one.
	 * 
	 * @param grammarSlot
	 * @param leftExtent
	 * @param rightExtent
	 * @return
	 */
	public NonPackedNode getNonPackedNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	/**
	 * 
	 * Returns the existing SPPF node with the given parameters if it exists, otherwise
	 * return null.
	 * 
	 * @param grammarSlot
	 * @param leftExtent
	 * @param rightExtent
	 * 
	 */
	public NonPackedNode hasNonPackedNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	/**
	 * 
	 * Returns an existing non-packed node that is equal to the provided key.
	 * 
	 * @param key
	 * @return
	 */
	public NonPackedNode getNonPackedNode(NonPackedNode key);
	
	/**
	 * 
	 * Returns the existing SPPF node equal to the given key, otherwise return null.
	 * 
	 * @param key
	 * @return
	 */
	public NonPackedNode hasNonPackedNode(NonPackedNode key);
	
	public void addPackedNode(NonPackedNode parent, GrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild);
	
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize);
	
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
	
	public Iterable<GSSEdge> getEdges(GSSNode src, GSSNode dst);

	public void addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode);
	
	public Iterable<NonPackedNode> getSPPFNodesOfPoppedElements(GSSNode gssNode);
	
	public int getNonPackedNodesCount();
	
	public int getPackedNodesCount();
	
	public int getGSSNodesCount();
	
	public int getGSSEdgesCount();
	
	public int getDescriptorsCount();
	
	public Iterable<GSSNode> getGSSNodes();
	
	public void init(Input input);
	
}	
