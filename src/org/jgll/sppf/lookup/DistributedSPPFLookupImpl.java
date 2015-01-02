package org.jgll.sppf.lookup;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.parser.lookup.AbstractSPPFLookup;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;


public class DistributedSPPFLookupImpl extends AbstractSPPFLookup {
	

	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getTerminalNode(new TerminalNode(slot, leftExtent, rightExtent), this::terminalNodeAdded);
	}

	@Override
	public TerminalNode findTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findTerminalNode(new TerminalNode(slot, leftExtent, rightExtent));
	}

	@Override
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getNonterminalNode(createNonterminalNode(slot, leftExtent, rightExtent), this::nonterminalNodeAdded);
	}

	@Override
	public NonterminalNode findNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findNonterminalNode(createNonterminalNode(slot, leftExtent, rightExtent));
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getIntermediateNode(new IntermediateNode(slot, leftExtent, rightExtent), this::intermediateNodeAdded);
	}

	@Override
	public IntermediateNode findIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findIntermediateNode(new IntermediateNode(slot, leftExtent, rightExtent));
	}

	@Override
	public NonterminalNode getStartSymbol(NonterminalGrammarSlot startSymbol, int inputSize) {
		return startSymbol.findNonterminalNode(createNonterminalNode(startSymbol, 0, inputSize - 1));
	}

}
