package org.iguana.sppf.lookup;

import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.util.collections.IntKey2PlusObject;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Input;
import org.iguana.util.collections.IntKey2;
import org.iguana.util.hashing.hashfunction.IntHash2;
import org.iguana.util.hashing.hashfunction.IntHash3;


public class DistributedSPPFLookupImpl extends AbstractSPPFLookup {
	
	private final IntHash2 f;
	private final IntHash3 f3;

	public DistributedSPPFLookupImpl(Input input) {
		super(input);
		int inputSize = input.length() + 1;
		this.f = (x, y) -> x * inputSize + y;
		this.f3 = (x, y, z) -> x * inputSize * inputSize + y * inputSize + z;
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

	@Override
	public <T> NonterminalNode getNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent, GSSNodeData<T> data) {
		return slot.getNonterminalNode(IntKey2PlusObject.from(data, leftExtent, rightExtent, f3), 
									   () -> createNonterminalNode(slot, leftExtent, rightExtent), 
									   this::nonterminalNodeAdded);
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent, Environment env) {
		return slot.getIntermediateNode(IntKey2PlusObject.from(env, leftExtent, rightExtent, f3), 
									    () -> createIntermediateNode(slot, leftExtent, rightExtent),
									    this::intermediateNodeAdded);
	}

}
