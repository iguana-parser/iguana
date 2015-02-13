package org.jgll.sppf.lookup;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.jgll.util.collections.IntKey2;
import org.jgll.util.hashing.hashfunction.IntHash2;


public class DistributedSPPFLookupImpl extends AbstractSPPFLookup {
	
	private IntHash2 f;

	public DistributedSPPFLookupImpl(Input input) {
		super(input);
		int inputSize = input.length() + 1;
		this.f = (x, y) -> x * inputSize + y;
	}
	
	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getTerminalNode(IntKey2.from(leftExtent, rightExtent, f), 
				                    () -> new TerminalNode(slot, leftExtent, rightExtent),
				                    this::terminalNodeAdded);
	}

	@Override
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getNonterminalNode(IntKey2.from(leftExtent, rightExtent, f), 
												   () -> createNonterminalNode(slot, leftExtent, rightExtent),
												   this::nonterminalNodeAdded);
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return slot.getIntermediateNode(IntKey2.from(leftExtent, rightExtent, f), 
				 						() -> createIntermediateNode(slot, leftExtent, rightExtent),
				 						this::intermediateNodeAdded);
	}

	@Override
	public NonterminalNode getStartSymbol(NonterminalGrammarSlot startSymbol, int inputSize) {
		return startSymbol.findNonterminalNode(IntKey2.from(0, inputSize - 1, f));
	}

}
