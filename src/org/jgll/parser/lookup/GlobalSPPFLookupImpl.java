package org.jgll.parser.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.NonterminalOrIntermediateNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFUtil;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.logging.LoggerWrapper;

public class GlobalSPPFLookupImpl implements SPPFLookup {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(GlobalSPPFLookupImpl.class);
	
	private final Map<TerminalNode, TerminalNode> tokenNodes;
	
	private final Map<NonterminalNode, NonterminalNode> nonterminalNodes;

	private final Map<IntermediateNode, IntermediateNode> intermediateNodes;
	
	private int countPackedNodes;
	
	private int countAmbiguousNodes;

	public GlobalSPPFLookupImpl() {
		SPPFUtil.initGlobal(HashFunctions.defaulFunction);
		nonterminalNodes = new HashMap<>();
		intermediateNodes = new HashMap<>();
		tokenNodes = new HashMap<>();
	}

	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int inputIndex, int length) {
		final TerminalNode key = new TerminalNode(slot.getRegularExpression(), inputIndex, length);
		return tokenNodes.computeIfAbsent(key, k -> { 
													  log.trace("Terminal node created: %s", key); 
													  return key; 
													});
	}
	
	@Override
	public TerminalNode findTerminalSymbolNode(TerminalGrammarSlot slot, int inputIndex, int length) {
		TerminalNode key = new TerminalNode(slot.getRegularExpression(), inputIndex, length);
		return tokenNodes.get(key);
	}

	@Override
	public NonterminalNode getNonterminalNode(HeadGrammarSlot head, int leftExtent, int rightExtent) {
		final NonterminalNode key = createNonterminalNode(head, leftExtent, rightExtent);
		return nonterminalNodes.computeIfAbsent(key, k -> { 
													        log.trace("Nonterminal node created: %s", key); 
															return key.init(); 
													      });
	}
	
	protected NonterminalNode createNonterminalNode(HeadGrammarSlot head, int leftExtent, int rightExtent) {
		if(head.getNonterminal().isEbnfList()) {
			return new ListSymbolNode(head, leftExtent, rightExtent);
		} else {
			return new NonterminalNode(head, leftExtent, rightExtent);
		}
	}
	
	protected IntermediateNode createIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return new IntermediateNode(grammarSlot, leftExtent, rightExtent);
	}

	@Override
	public NonterminalNode findNonterminalNode(HeadGrammarSlot head, int leftExtent, int rightExtent) {		
		NonterminalNode key = createNonterminalNode(head, leftExtent, rightExtent);
		return nonterminalNodes.get(key);
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		final IntermediateNode key = createIntermediateNode(grammarSlot, leftExtent, rightExtent);
		return intermediateNodes.computeIfAbsent(key, k -> { 
														      log.trace("Intermediate node created: %s", key); 
														      return key.init(); 
														   });
	}

	@Override
	public IntermediateNode findIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		IntermediateNode key = createIntermediateNode(grammarSlot, leftExtent, rightExtent);
		return intermediateNodes.get(key);
	}
	
	@Override
	public void addPackedNode(NonPackedNode parent, BodyGrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		PackedNode packedNode = new PackedNode(slot, pivot, parent);
		boolean ambiguousBefore = parent.isAmbiguous();
		if (((NonterminalOrIntermediateNode) parent).addPackedNode(packedNode, leftChild, rightChild)) {
			countPackedNodes++;
			boolean ambiguousAfter = parent.isAmbiguous();
			if (!ambiguousBefore && ambiguousAfter) {
//				log.warning("Ambiguity at line: %d, column: %d \n %s\n %s \n %s",
//						input.getLineNumber(parent.getLeftExtent()),
//						input.getColumnNumber(parent.getLeftExtent()),
//						grammar.getGrammarSlot(slot.getId()), 
//						grammar.getGrammarSlot(parent.getFirstPackedNodeGrammarSlot()),
//						input.subString(parent.getLeftExtent(), parent.getRightExtent()));
				countAmbiguousNodes++;
//				Visualization.generateSPPFGraph("/Users/aliafroozeh/output", parent, grammar, input);
//				System.exit(0);
			}
		}
	}
	
	@Override
	public NonterminalNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize) {
		return nonterminalNodes.get(createNonterminalNode(startSymbol, 0, inputSize - 1));
	}

	@Override
	public int getNonterminalNodesCount() {
		return nonterminalNodes.size();
	}

	@Override
	public int getIntermediateNodesCount() {
		return intermediateNodes.size();
	}

	@Override
	public int getTokenNodesCount() {
		return tokenNodes.size();
	}

	@Override
	public int getPackedNodesCount() {
		return countPackedNodes;
	}
	
	@Override
	public int getAmbiguousNodesCount() {
		return countAmbiguousNodes;
	}
}
