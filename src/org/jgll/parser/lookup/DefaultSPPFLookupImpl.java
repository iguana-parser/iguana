package org.jgll.parser.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.OriginalIntermediateNode;
import org.jgll.sppf.OriginalNonterminalNode;
import org.jgll.sppf.lookup.GlobalSPPFLookupImpl;
import org.jgll.util.hashing.ExternalHashEquals;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class DefaultSPPFLookupImpl extends GlobalSPPFLookupImpl {

	public DefaultSPPFLookupImpl(HashFunction f) {
		super(f);
	}
	
	@Override
	public NonterminalNode createNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent, ExternalHashEquals<NonPackedNode> hashEquals) {
		return new OriginalNonterminalNode(slot, leftExtent, rightExtent, hashEquals);
	}
	
	@Override
	protected IntermediateNode createIntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		return new OriginalIntermediateNode(slot, leftExtent, rightExtent, hashEquals);
	}

}
