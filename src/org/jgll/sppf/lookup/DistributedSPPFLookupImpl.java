package org.jgll.sppf.lookup;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.hashing.IntKey2;
import org.jgll.util.hashing.hashfunction.HashFunction;


public class DistributedSPPFLookupImpl extends AbstractSPPFLookup {
	
	private HashFunction f;

	public DistributedSPPFLookupImpl(HashFunction f) {
		this.f = f;
	}
	
	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getTerminalNode(IntKey2.from(leftExtent, rightExtent, f), 
				                    () -> new TerminalNode(slot, leftExtent, rightExtent),
				                    this::terminalNodeAdded);
	}

	@Override
	public TerminalNode findTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findTerminalNode(IntKey2.from(leftExtent, rightExtent, f));
	}

	@Override
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getNonterminalNode(IntKey2.from(leftExtent, rightExtent, f), 
												   () -> createNonterminalNode(slot, leftExtent, rightExtent),
												   this::nonterminalNodeAdded);
	}

	@Override
	public NonterminalNode findNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findNonterminalNode(IntKey2.from(leftExtent, rightExtent, f));
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getIntermediateNode(IntKey2.from(leftExtent, rightExtent, f), 
				 						() -> createIntermediateNode(slot, leftExtent, rightExtent),
				 						this::intermediateNodeAdded);
	}

	@Override
	public IntermediateNode findIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findIntermediateNode(IntKey2.from(leftExtent, rightExtent, f));
	}

	@Override
	public NonterminalNode getStartSymbol(NonterminalGrammarSlot startSymbol, int inputSize) {
		return startSymbol.findNonterminalNode(IntKey2.from(0, inputSize - 1, f));
	}

	protected IntermediateNode createIntermediateNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return new IntermediateNode(grammarSlot, leftExtent, rightExtent);
	}

}
