package org.jgll.parser.lookup;

import java.util.ArrayDeque;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.IguanaSet;
import org.jgll.util.logging.LoggerWrapper;

public class SPPFLookupImpl implements SPPFLookup {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(SPPFLookupImpl.class);
	
	private final TokenSymbolNode[][] tokenSymbolNodes;
	
	private final IguanaSet<NonterminalSymbolNode>[] nonterminalNodes;

	private final IguanaSet<NonPackedNode>[] intermediateNodes;
	
	public SPPFLookupImpl(Grammar grammar, Input input) {
		long start = System.nanoTime();

		nonterminalNodes = new IguanaSet[input.length()];
		
		intermediateNodes = new IguanaSet[input.length()];

		tokenSymbolNodes = new TokenSymbolNode[grammar.getCountTokens()][input.length()];

		long end = System.nanoTime();
		log.info("Lookup table initialization: %d ms", (end - start) / 1000_000);
	}

	@Override
	public TokenSymbolNode getTokenSymbolNode(int tokenID, int inputIndex, int length) {
		return null;
	}

	@Override
	public NonterminalSymbolNode getNonterminalNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return null;
	}

	@Override
	public NonterminalSymbolNode hasNonterminalNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return null;
	}

	@Override
	public NonterminalSymbolNode getIntermediateNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return null;
	}

	@Override
	public NonterminalSymbolNode hasIntermediateNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return null;
	}

	@Override
	public NonPackedNode getNonPackedNode(NonPackedNode key) {
		return null;
	}

	@Override
	public NonPackedNode hasNonPackedNode(NonPackedNode key) {
		return null;
	}

	@Override
	public void addPackedNode(NonPackedNode parent, GrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		
	}

	@Override
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize) {
		return null;
	}

	@Override
	public int getNonterminalNodesCount() {
		return 0;
	}

	@Override
	public int getIntermediateNodesCount() {
		return 0;
	}

	@Override
	public int getTokenNodesCount() {
		return 0;
	}

	@Override
	public int getPackedNodesCount() {
		return 0;
	}

}
