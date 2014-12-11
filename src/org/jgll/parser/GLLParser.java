package org.jgll.parser;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface GLLParser {
	
	public ParseResult parse(Input input, GrammarGraph grammar, String startSymbolName);
	
	public GrammarSlot pop();
	
	public GrammarSlot create(BodyGrammarSlot slot, HeadGrammarSlot head);
	
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int inputIndex, int rightExtent);
	
	/**
	 * @return true if no such descriptor exists.
	 */
	public boolean hasDescriptor(Descriptor descriptor);
	
	public void scheduleDescriptor(Descriptor descriptor);
	
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
	
	public GrammarGraph getGrammar();
	
	public Input getInput();
	
	/**
	 * Current descriptor being processed.
	 */
	public Descriptor getCurrentDescriptor();
	
}
