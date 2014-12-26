package org.jgll.parser;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface GLLParser {
	
	public ParseResult parse(Input input, GrammarGraph grammar, String startSymbolName);
	
	public void pop();
	
	public NonterminalGrammarSlot create(GrammarSlot returnSlot, NonterminalGrammarSlot head);
		
	public boolean hasDescriptor(Descriptor descriptor);
	
	public void scheduleDescriptor(Descriptor descriptor);
	
	default boolean addDescriptor(Descriptor descriptor) {
		if (hasDescriptor(descriptor)) {
			scheduleDescriptor(descriptor);
			return true;
		}
		return false;
	}
	
	public boolean hasNextDescriptor();
	
	/**
	 * Reads the next descriptor and sets the state of the parser to it.
	 */
	public Descriptor nextDescriptor();
	
	public int getCurrentInputIndex();
	
	public GSSNode getCurrentGSSNode();
	
	public NonPackedNode getCurrentSPPFNode();
	
	public void setCurrentSPPFNode(NonPackedNode node);
	
	public void setCurrentInputIndex(int inputIndex);
	
	public void recordParseError(GrammarSlot slot);
	
	public GSSLookup getGSSLookup();
	
	public SPPFLookup getSPPFLookup();
	
	public Input getInput();
	
	public GrammarSlotRegistry getRegistry();
	
	/**
	 * Current descriptor being processed.
	 */
	public Descriptor getCurrentDescriptor();
	
}
