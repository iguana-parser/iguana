package org.jgll.parser.lookup;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

public interface SPPFLookup {

	/**
	 * 
	 * @param tokenID
	 * @param inputIndex
	 * @param length
	 * @return
	 */
	public TokenSymbolNode getTokenSymbolNode(int tokenID, int inputIndex, int length);
	
	/**
	 * 
	 * @param tokenID
	 * @param inputIndex
	 * @param length
	 * @return
	 */
	public TokenSymbolNode findTokenSymbolNode(int tokenID, int inputIndex, int length);
	
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
	public NonterminalSymbolNode getNonterminalNode(HeadGrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	/**
	 * 
	 * Returns the existing SPPF node with the given parameters if it exists, otherwise
	 * return null.
	 * 
	 * @param grammarSlot
	 * @param leftExtent
	 * @param rightExtent
	 * 
	 * @return null if no nonterminal node is found with the given parameters
	 * 
	 */
	public NonterminalSymbolNode findNonterminalNode(HeadGrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	public IntermediateNode findIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent);
		
	public void addPackedNode(NonterminalSymbolNode parent, LastGrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild);
	
	public void addPackedNode(NonterminalSymbolNode parent, LastGrammarSlot slot, int pivot, SPPFNode child);
	
	public void addPackedNode(IntermediateNode parent, BodyGrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild);
	
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize);
	
	public int getNonterminalNodesCount();
	
	public int getIntermediateNodesCount();
	
	public int getTokenNodesCount();
	
	public int getPackedNodesCount();
	
}
