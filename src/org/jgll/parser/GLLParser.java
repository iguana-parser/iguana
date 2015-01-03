package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EndGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Configuration;
import org.jgll.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface GLLParser {
	
	public ParseResult parse(Input input, Grammar grammar, String startSymbolName, Configuration config);
	
	default ParseResult parse(Input input, Grammar grammar, String startSymbolName) {
		return parse(input, grammar, startSymbolName, Configuration.builder().build());
	}
	
	public void pop(GSSNode gssNode, int inputIndex, NonPackedNode node);
	
	public GSSNode create(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, GSSNode gssNode, int i, NonPackedNode node);
	
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent);

	public TerminalNode getEpsilonNode(int inputIndex);
	
	public NonterminalNode getNonterminalNode(EndGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild);
	
	public NonterminalNode getNonterminalNode(EndGrammarSlot slot, NonPackedNode child);
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild);
	
	public boolean hasDescriptor(Descriptor descriptor);
	
	public void scheduleDescriptor(Descriptor descriptor);
	
	default boolean addDescriptor(Descriptor descriptor) {
		if (!hasDescriptor(descriptor)) {
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
	
	public void recordParseError(GrammarSlot slot);
	
	public Input getInput();
	
	public GrammarSlotRegistry getRegistry();
	
	public void reset();
	
}
