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
import org.jgll.sppf.IntermediateNode;
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
	 */
	public void createGSSNode(BodyGrammarSlot slot, HeadGrammarSlot head);
	
	public boolean hasGSSNode(BodyGrammarSlot slot, HeadGrammarSlot head);
	
	public TokenSymbolNode getTokenNode(int tokenID, int inputIndex, int length);
	
	/**
	 * 
	 * Returns an existing nonterminal node with the given slot, left and right children,
	 * or creates one if no such node exists.
	 * 
	 * @param slot
	 * @param leftChild
	 * @param rightChild
	 * 
	 */
	public NonterminalSymbolNode getNonterminalNode(LastGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild);
	
	/**
	 * Returns an existing nonterminal node with the given slot and its only child, or creates one
	 * if no such node exists.
	 * 
	 * @param slot
	 * @param child
	 * @return
	 */
	public NonterminalSymbolNode getNonterminalNode(LastGrammarSlot slot, SPPFNode child);
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild);
	
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
		
}
