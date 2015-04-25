package org.jgll.sppf.lookup;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.OriginalPackedNodeSet;
import org.jgll.util.Input;

public class OriginalGlobalSPPFLookupImpl extends GlobalSPPFLookupImpl {

	private int inputSize;

	public OriginalGlobalSPPFLookupImpl(Input input, Grammar grammar) {
		super(input, grammar);
		this.inputSize = input.length() + 1;
	}
	
	@Override
	public NonterminalNode createNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return new NonterminalNode(slot, leftExtent, rightExtent, new OriginalPackedNodeSet(inputSize));
	}
	
	@Override
	protected IntermediateNode createIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return new IntermediateNode(slot, leftExtent, rightExtent, new OriginalPackedNodeSet(inputSize));
	}

}
