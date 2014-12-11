package org.jgll.parser.lookup;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalNode;

public interface SPPFLookup {

	/**
	 * @param terminal
	 * @param inputIndex
	 * @param length
	 * @return
	 */
	public TerminalNode getTerminalSymbolNode(TerminalGrammarSlot slot, int inputIndex, int rightExtent);
	
	public TerminalNode getEpsilonNode(int inputIndex);
	
	/**
	 * 
	 * @param tokenID
	 * @param inputIndex
	 * @param length
	 * @return
	 */
	public TerminalNode findTerminalSymbolNode(TerminalGrammarSlot slot, int inputIndex, int rightExtent);
	
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
	
	public void addPackedNode(NonPackedNode parent, BodyGrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild);
		
	public NonterminalNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize);
	
	public int getNonterminalNodesCount();
	
	public int getIntermediateNodesCount();
	
	public int getTokenNodesCount();
	
	public int getPackedNodesCount();
	
	public int getAmbiguousNodesCount();
	
}
