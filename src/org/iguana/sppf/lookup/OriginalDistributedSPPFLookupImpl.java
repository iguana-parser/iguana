package org.iguana.sppf.lookup;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.OriginalPackedNodeSet;
import org.iguana.util.Input;

public class OriginalDistributedSPPFLookupImpl extends DistributedSPPFLookupImpl {

	private int inputSize;

	public OriginalDistributedSPPFLookupImpl(Input input) {
		super(input);
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
