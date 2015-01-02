package org.jgll.sppf.lookup;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.hashing.ExternalHashEquals;
import org.jgll.util.hashing.hashfunction.HashFunction;


public class DistributedSPPFLookupImpl extends AbstractSPPFLookup {
	
	private final ExternalHashEquals<NonPackedNode> hashEquals; 
	
	public DistributedSPPFLookupImpl(HashFunction f) {
		this.hashEquals = NonPackedNode.distributedHashEquals(f);
	}
	
	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getTerminalNode(new TerminalNode(slot, leftExtent, rightExtent, hashEquals), this::terminalNodeAdded);
	}

	@Override
	public TerminalNode findTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findTerminalNode(new TerminalNode(slot, leftExtent, rightExtent, hashEquals));
	}

	@Override
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getNonterminalNode(createNonterminalNode(slot, leftExtent, rightExtent, hashEquals), this::nonterminalNodeAdded);
	}

	@Override
	public NonterminalNode findNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findNonterminalNode(createNonterminalNode(slot, leftExtent, rightExtent, hashEquals));
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getIntermediateNode(new IntermediateNode(slot, leftExtent, rightExtent, hashEquals), this::intermediateNodeAdded);
	}

	@Override
	public IntermediateNode findIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.findIntermediateNode(new IntermediateNode(slot, leftExtent, rightExtent, hashEquals));
	}

	@Override
	public NonterminalNode getStartSymbol(NonterminalGrammarSlot startSymbol, int inputSize) {
		return startSymbol.findNonterminalNode(createNonterminalNode(startSymbol, 0, inputSize - 1, hashEquals));
	}

}
