package org.jgll.parser.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

public interface SPPFLookup {

	public TokenSymbolNode getTokenSymbolNode(int tokenID, int inputIndex, int length);
	
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
	public NonterminalSymbolNode getNonterminalNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
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
	public NonterminalSymbolNode hasNonterminalNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	public NonterminalSymbolNode getIntermediateNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	public NonterminalSymbolNode hasIntermediateNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
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
	
	public int getNonterminalNodesCount();
	
	public int getIntermediateNodesCount();
	
	public int getTokenNodesCount();
	
	public int getPackedNodesCount();
	
}
