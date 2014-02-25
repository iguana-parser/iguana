package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface GLLParser {
	
	public NonterminalSymbolNode parse(GLLLexer lexer, Grammar grammar, String startSymbolName) throws ParseError;
	
	public NonterminalSymbolNode parse(Input input, Grammar grammar, String startSymbolName) throws ParseError;
	
	public void pop();
	
	/**
	 * Creates a new GSSNode with the given grammar slot.
	 * 
	 * @param slot
	 */
	public void createGSSNode(BodyGrammarSlot slot, HeadGrammarSlot head);
	
	public TokenSymbolNode getTokenNode(int tokenID, int inputIndex, int length);
	
	public void getNonterminalNode(LastGrammarSlot slot, SPPFNode rightChild);
	
	public void getIntermediateNode(BodyGrammarSlot slot, SPPFNode rightChild);
	
	/**
	 * Adds a descriptor with the current input index, current GSS node and a 
	 * dummy node. 
	 */
	public void addDescriptor(GrammarSlot slot);
	
	public void addDescriptor(GrammarSlot slot, GSSNode currentGSSNode, int inputIndex, SPPFNode currentNode);
	
	public void addDescriptor(GrammarSlot slot, GSSNode currentGSSNode, int inputIndex, SPPFNode currentNode, Object object);
	
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
	
	public GSSLookup getGSSLookup();
	
	public SPPFLookup getSPPFLookup();
	
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
	
	public long getParsingTime();
	
}
