package org.jgll.lookup;

import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.LastGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GSSNode;
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
	
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol);
	
	public boolean hasGSSEdge(GSSNode source, SPPFNode label, GSSNode destination);

	public GSSNode getGSSNode(GrammarSlot label, int inputIndex);
	
	public void addToPoppedElements(GSSNode gssNode, SPPFNode sppfNode);
	
	public Iterable<SPPFNode> getSPPFNodesOfPoppedElements(GSSNode gssNode);
	
	public int getNonPackedNodesCount();
	
	public int getGSSNodesCount();
	
	public int getGSSEdgesCount();
	
	public int getDescriptorsCount();
	
	/**
	 *  
	 * 
	 * @param slot
	 * 
	 * @return returns true if the given slot has been popped.
	 */
	public boolean isPopped(LastGrammarSlot slot);
	
	public void setPopped(LastGrammarSlot slot);
	
	public void clearPopped(LastGrammarSlot slot);
	
	public Iterable<GSSNode> getGSSNodes();
	
}
