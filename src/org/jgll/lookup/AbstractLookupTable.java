package org.jgll.lookup;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;

/**
 * 
 * This class provides a skeletal implementation of {@link LookupTable}.
 * Based on how descriptors are removed from the set of
 * descriptors, e.g., using a stack or a queue, the implementation of
 * LookupTables may vary. This class only provides an implementation for 
 * lookup functionalities which are the same for different strategies.
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class AbstractLookupTable implements LookupTable {
	
	protected final Grammar grammar;
	
	protected final int slotsSize;
	
	
	public AbstractLookupTable(Grammar grammar) {
		this.grammar = grammar;
		this.slotsSize = grammar.getGrammarSlots().size();
	}
		
	protected NonPackedNode createNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		NonPackedNode key;
		if(slot instanceof HeadGrammarSlot) {
			HeadGrammarSlot head = (HeadGrammarSlot) slot;
			if(head.getNonterminal().isEbnfList()) {
				key = new ListSymbolNode(slot, leftExtent, rightExtent);
			} else {
				key = new NonterminalSymbolNode(slot, leftExtent, rightExtent);
			}
		} else {
			key = new IntermediateNode(slot, leftExtent, rightExtent);
		}
		return key;
	}	
}
