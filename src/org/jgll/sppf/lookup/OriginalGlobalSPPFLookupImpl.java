package org.jgll.sppf.lookup;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.OriginalPackedNodeSet;

public class OriginalGlobalSPPFLookupImpl extends GlobalSPPFLookupImpl {

	private int inputSize;

	public OriginalGlobalSPPFLookupImpl(int inputSize, int grammarSize) {
		super(inputSize, grammarSize);
		this.inputSize = inputSize;
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
