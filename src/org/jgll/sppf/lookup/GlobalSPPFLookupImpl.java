package org.jgll.sppf.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.hashing.ExternalHashEquals;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class GlobalSPPFLookupImpl extends AbstractSPPFLookup {
	
	protected final ExternalHashEquals<NonPackedNode> hashEquals; 
	
	private Map<TerminalNode, TerminalNode> terminalNodes;
	
	private Map<NonterminalNode, NonterminalNode> nonterminalNodes;

	private Map<IntermediateNode, IntermediateNode> intermediateNodes;	

	public GlobalSPPFLookupImpl(HashFunction f) {
		this.nonterminalNodes = new HashMap<>();
		this.intermediateNodes = new HashMap<>();
		this.terminalNodes = new HashMap<>();
		this.hashEquals = NonPackedNode.globalHashEquals(f);
	}
	
	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int inputIndex, int rightExtent) {
		TerminalNode key = new TerminalNode(slot, inputIndex, rightExtent, hashEquals);
		TerminalNode val;
		if ((val = terminalNodes.get(key)) == null) {
			val = key;
			terminalNodes.put(key, val);
		}
		return val;
	}
	
	@Override
	public TerminalNode findTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		TerminalNode key = new TerminalNode(slot, leftExtent, rightExtent, hashEquals);
		return terminalNodes.get(key);
	}

	@Override
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent) {
		NonterminalNode key = createNonterminalNode(head, leftExtent, rightExtent, hashEquals);
		NonterminalNode val;
		if ((val = nonterminalNodes.get(key)) == null) {
			val = key.init();
			nonterminalNodes.put(key, val);
		}
		return val;
	}
	
	protected IntermediateNode createIntermediateNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return new IntermediateNode(grammarSlot, leftExtent, rightExtent, hashEquals);
	}

	@Override
	public NonterminalNode findNonterminalNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent) {		
		NonterminalNode key = createNonterminalNode(head, leftExtent, rightExtent, hashEquals);
		return nonterminalNodes.get(key);
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		IntermediateNode key = createIntermediateNode(grammarSlot, leftExtent, rightExtent);
		IntermediateNode val;
		if ((val = intermediateNodes.get(key)) == null) {
			val = key.init();
			intermediateNodes.put(key, val);
		}
		return val;
	}

	@Override
	public IntermediateNode findIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		IntermediateNode key = createIntermediateNode(grammarSlot, leftExtent, rightExtent);
		return intermediateNodes.get(key);
	}
		
	@Override
	public NonterminalNode getStartSymbol(NonterminalGrammarSlot startSymbol, int inputSize) {
		return nonterminalNodes.get(createNonterminalNode(startSymbol, 0, inputSize - 1, hashEquals));
	}
	
	@Override
	public void reset() {
		nonterminalNodes = new HashMap<>();
		intermediateNodes = new HashMap<>();
		terminalNodes = new HashMap<>();
	}

}
