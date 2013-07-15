package org.jgll.lookup;

import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

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
	
	public SPPFNode getNonPackedNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	public void addPackedNode(NonPackedNode parent, GrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild);
	
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol);
	
	public boolean hasGSSEdge(GSSNode source, SPPFNode label, GSSNode destination);

	public GSSNode getGSSNode(GrammarSlot label, int inputIndex);
	
	public void addToPoppedElements(GSSNode gssNode, SPPFNode sppfNode);
	
	public Iterable<SPPFNode> getSPPFNodesOfPoppedElements(GSSNode gssNode);
	
	public int getNonPackedNodesCount();
	
	public int getPackedNodesCount();
	
	public int getGSSNodesCount();
	
	public int getGSSEdgesCount();
	
	public int getDescriptorsCount();
	
	public Iterable<GSSNode> getGSSNodes();
	
}	
