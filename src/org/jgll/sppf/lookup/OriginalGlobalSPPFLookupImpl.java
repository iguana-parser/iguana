package org.jgll.sppf.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.OriginalIntermediateNode;
import org.jgll.sppf.OriginalNonterminalNode;

public class OriginalGlobalSPPFLookupImpl extends GlobalSPPFLookupImpl {

	public OriginalGlobalSPPFLookupImpl(int inputSize, int grammarSize) {
		super(inputSize, grammarSize);
	}
	
	@Override
	public NonterminalNode createNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return new OriginalNonterminalNode(slot, leftExtent, rightExtent);
	}
	
	@Override
	protected IntermediateNode createIntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		return new OriginalIntermediateNode(slot, leftExtent, rightExtent);
	}

}
