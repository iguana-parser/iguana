package org.iguana.sppf.lookup;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.OriginalPackedNodeSet;
import org.iguana.util.Input;

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
