package org.jgll.parser.lookup;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.OriginalNonterminalNode;
import org.jgll.util.Input;

public class DefaultSPPFLookupImpl extends SPPFLookupImpl {

	public DefaultSPPFLookupImpl(GrammarGraph grammar, Input input) {
		super(grammar, input);
	}
	
	@Override
	protected NonterminalNode createNonterminalNode(Nonterminal nonterminal, int nonterminalId, int leftExtent, int rightExtent) {
		return new OriginalNonterminalNode(nonterminalId, leftExtent, rightExtent);
	}
	
	@Override
	protected IntermediateNode createIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return new IntermediateNode(grammarSlot.getId(), leftExtent, rightExtent);
	}

}
