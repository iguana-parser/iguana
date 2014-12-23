package org.jgll.parser.lookup;

import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;

public interface SPPFLookup {

	/**
	 * @param terminal
	 * @param leftExtent
	 * @param rightExtent
	 * @return
	 */
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent);
	
	
	default TerminalNode getEpsilonNode(int inputIndex) {
		return getTerminalNode(Epsilon.TOKEN_ID, inputIndex, inputIndex);
	}
	
	/**
	 * 
	 * @param tokenID
	 * @param leftExtent
	 * @param rightExtent
	 * @return
	 */
	public TerminalNode findTerminalSymbolNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent);
	
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
	public NonterminalNode getNonterminalNode(HeadGrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
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
	public NonterminalNode findNonterminalNode(HeadGrammarSlot slot, int leftExtent, int rightExtent);
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent);
	
	public IntermediateNode findIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent);
	
	public void addPackedNode(NonPackedNode parent, BodyGrammarSlot slot, int pivot, NonPackedNode leftChild, NonPackedNode rightChild);
		
	public NonterminalNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize);
	
	public int getNonterminalNodesCount();
	
	public int getIntermediateNodesCount();
	
	public int getTokenNodesCount();
	
	public int getPackedNodesCount();
	
	public int getAmbiguousNodesCount();
	
}
