package org.jgll.sppf.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.collections.IntKey3;
import org.jgll.util.collections.Key;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class GlobalSPPFLookupImpl extends AbstractSPPFLookup {
	
	private Map<Key, TerminalNode> terminalNodes;
	
	private Map<Key, NonterminalNode> nonterminalNodes;

	private Map<Key, IntermediateNode> intermediateNodes;

	private HashFunction f;	

	public GlobalSPPFLookupImpl(HashFunction f) {
		this.f = f;
		this.nonterminalNodes = new HashMap<>();
		this.intermediateNodes = new HashMap<>();
		this.terminalNodes = new HashMap<>();
	}
	
	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		IntKey3 key = IntKey3.from(slot.getId(), leftExtent, rightExtent, f);
		TerminalNode val;
		if ((val = terminalNodes.get(key)) == null) {
			val = new TerminalNode(slot, leftExtent, rightExtent);
			terminalNodes.put(key, val);
		}
		return val;
	}
	
	@Override
	public TerminalNode findTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		TerminalNode key = new TerminalNode(slot, leftExtent, rightExtent);
		return terminalNodes.get(key);
	}

	@Override
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent) {
		IntKey3 key = IntKey3.from(head.getId(), leftExtent, rightExtent, f); 
		NonterminalNode val;
		if ((val = nonterminalNodes.get(key)) == null) {
			val = createNonterminalNode(head, leftExtent, rightExtent);;
			nonterminalNodeAdded(val);
			nonterminalNodes.put(key, val);
		}
		return val;
	}
	
	protected IntermediateNode createIntermediateNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return new IntermediateNode(grammarSlot, leftExtent, rightExtent);
	}

	@Override
	public NonterminalNode findNonterminalNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent) {		
		NonterminalNode key = createNonterminalNode(head, leftExtent, rightExtent);
		return nonterminalNodes.get(key);
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		IntKey3 key = IntKey3.from(slot.getId(), leftExtent, rightExtent, f); 
		IntermediateNode val;
		if ((val = intermediateNodes.get(key)) == null) {
			val = createIntermediateNode(slot, leftExtent, rightExtent);
			intermediateNodeAdded(val);
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
		return nonterminalNodes.get(IntKey3.from(startSymbol.getId(), 0, inputSize - 1, f));
	}
	
	@Override
	public void reset() {
		nonterminalNodes = new HashMap<>();
		intermediateNodes = new HashMap<>();
		terminalNodes = new HashMap<>();
	}

}
