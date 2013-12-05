package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.parser.lookup.LookupTable;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.RegularExpressionNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

public interface GLLParserInternals {
	
	public void pop();
	
	/**
	 * Creates a new GSSNode with the given grammar slot.
	 * 
	 * @param slot
	 */
	public void createGSSNode(BodyGrammarSlot slot, HeadGrammarSlot head);
	
	public TerminalSymbolNode getTerminalNode(int c);
	
	public TerminalSymbolNode getEpsilonNode();
	
	public void getNonterminalNode(LastGrammarSlot slot, SPPFNode rightChild);
	
	public void getIntermediateNode(BodyGrammarSlot slot, SPPFNode rightChild);
	
	/**
	 * Adds a descriptor with the current input index, current GSS node and a 
	 * dummy node. 
	 */
	public void addDescriptor(GrammarSlot slot);
	
	public void addDescriptor(GrammarSlot slot, GSSNode currentGSSNode, int inputIndex, SPPFNode currentNode);
	
	public void addDescriptor(GrammarSlot slot, GSSNode currentGSSNode, int inputIndex, SPPFNode currentNode, Object object);
	
	public NonPackedNode getKeywordStub(Keyword keyword, HeadGrammarSlot slot, int ci);
	
	public RegularExpressionNode getRegularExpressionNode(BodyGrammarSlot slot, int leftExtent, int rightExtent);
	
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
	
	public GSSNode getCurrentGSSNode();
	
	public SPPFNode getCurrentSPPFNode();
	
	public void setCurrentSPPFNode(SPPFNode node);
	
	public void recordParseError(GrammarSlot slot);
	
	public LookupTable getLookupTable();
	
	public Grammar getGrammar();
	
	/**
	 * Current descriptor being processed.
	 */
	public Descriptor getCurrentDescriptor();
	
	/**
	 * Ring size is the length of largest chain of terminals in the body of production
	 * rules. It can be derived automatically from the grammar, but the user can also
	 * set it. Manual setting of this property is useful when regular lists are 
	 * eagerly parsed.
	 * 
	 * @return {@link Integer#MAX_VALUE} when the parser is in the recursive descent mode.
	 */
	public int getRegularListLength();
	
	public boolean isLLOptimizationEnabled();
	
}
