package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.lookup.LookupTable;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface GLLParser {
	
	public NonterminalSymbolNode parse(Input input, Grammar grammar, String startSymbolName) throws ParseError;
	
	public void pop();
	
	/**
	 * Creates a new GSSNode with the given grammar slot.
	 * 
	 * @param slot
	 */
	public void createGSSNode(GrammarSlot slot);
	
	public TerminalSymbolNode getTerminalNode(int c);
	
	public TerminalSymbolNode getEpsilonNode();
	
	public SPPFNode getNodeP(BodyGrammarSlot slot, SPPFNode rightChild);
	
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
