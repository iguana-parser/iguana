package org.jgll.parser.lookup;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.OriginalIntermediateNode;
import org.jgll.sppf.OriginalNonterminalNode;

public class DefaultSPPFLookupImpl extends GlobalSPPFLookupImpl {

	@Override
	protected NonterminalNode createNonterminalNode(HeadGrammarSlot slot, int leftExtent, int rightExtent) {
		return new OriginalNonterminalNode(slot, leftExtent, rightExtent);
	}
	
	@Override
	protected IntermediateNode createIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return new OriginalIntermediateNode(slot, leftExtent, rightExtent);
	}

}
