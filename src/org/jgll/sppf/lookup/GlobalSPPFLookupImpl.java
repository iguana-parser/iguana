package org.jgll.sppf.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.parser.lookup.AbstractSPPFLookup;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;

public class GlobalSPPFLookupImpl extends AbstractSPPFLookup {
	
	private final Map<TerminalNode, TerminalNode> terminalNodes;
	
	private final Map<NonterminalNode, NonterminalNode> nonterminalNodes;

	private final Map<IntermediateNode, IntermediateNode> intermediateNodes;	

	public GlobalSPPFLookupImpl() {
		nonterminalNodes = new HashMap<>();
		intermediateNodes = new HashMap<>();
		terminalNodes = new HashMap<>();
	}

	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int inputIndex, int rightExtent) {
		final TerminalNode key = new TerminalNode(slot, inputIndex, rightExtent);
		return terminalNodes.computeIfAbsent(key, k -> { terminalNodeAdded(k);  return k; });
	}
	
	@Override
	public TerminalNode findTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		TerminalNode key = new TerminalNode(slot, leftExtent, rightExtent);
		return terminalNodes.get(key);
	}

	@Override
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot head, int leftExtent, int rightExtent) {
		final NonterminalNode key = createNonterminalNode(head, leftExtent, rightExtent);
		return nonterminalNodes.computeIfAbsent(key, k -> { nonterminalNodeAdded(k); return k.init(); });
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
	public IntermediateNode getIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		final IntermediateNode key = createIntermediateNode(grammarSlot, leftExtent, rightExtent);
		return intermediateNodes.computeIfAbsent(key, k -> { intermediateNodeAdded(key);  return k.init(); });
	}

	@Override
	public IntermediateNode findIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		IntermediateNode key = createIntermediateNode(grammarSlot, leftExtent, rightExtent);
		return intermediateNodes.get(key);
	}
		
	@Override
	public NonterminalNode getStartSymbol(NonterminalGrammarSlot startSymbol, int inputSize) {
		return nonterminalNodes.get(createNonterminalNode(startSymbol, 0, inputSize - 1));
	}

}
