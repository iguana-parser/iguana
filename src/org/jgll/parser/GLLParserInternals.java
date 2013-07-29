package org.jgll.parser;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.lookup.LookupTable;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

public interface GLLParserInternals {
	
	public void pop();
	
	/**
	 * Creates a new GSSNode with the given grammar slot.
	 * 
	 * @param slot
	 */
	public void createGSSNode(GrammarSlot slot);
	
	public TerminalSymbolNode getTerminalNode(int c);
	
	public TerminalSymbolNode getEpsilonNode();
	
	public SPPFNode getNonterminalNode(LastGrammarSlot slot, SPPFNode rightChild);
	
	public SPPFNode getIntermediateNode(BodyGrammarSlot slot, SPPFNode rightChild);
	
	public void addDescriptor(GrammarSlot label);
	
	/**
	 * 
	 * @return
	 */
	public boolean hasNextDescriptor();
	
	/**
	 * Reads the next descriptor and sets the state of the parser to it.
	 */
	public Descriptor nextDescriptor();
	
	public int getCurrentInputIndex();
	
	public void recordParseError(GrammarSlot slot);
	
	public LookupTable getLookupTable();

}
