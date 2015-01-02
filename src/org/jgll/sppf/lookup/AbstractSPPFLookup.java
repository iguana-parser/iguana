package org.jgll.sppf.lookup;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.NonterminalOrIntermediateNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.logging.LoggerWrapper;


public abstract class AbstractSPPFLookup implements SPPFLookup {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractSPPFLookup.class);
	
	private int countAmbiguousNodes;
	
	private int countNonterminalNodes;
	
	private int countIntermediateNodes;

	private int countPackedNodes;
	
	private int countTerminalNodes;
	
	@Override
	public void intermediateNodeAdded(IntermediateNode node) {
		log.trace("Intermediate node created: %s", node);
		node.init();
		countIntermediateNodes++;
	}
	
	@Override
	public void nonterminalNodeAdded(NonterminalNode node) {
		log.trace("Nonterminal node created: %s", node);
		node.init();
		countNonterminalNodes++;
	}
	
	@Override
	public void ambiguousNodeAdded(NonterminalOrIntermediateNode node) {
		countAmbiguousNodes++;
	}
	
	@Override
	public void packedNodeAdded(PackedNode node) {
		countPackedNodes++;
	}
	
	@Override
	public void terminalNodeAdded(TerminalNode node) {
		log.trace("Terminal node created: %s", node);
		countTerminalNodes++;
	}
	
	public int getNonterminalNodesCount() {
		return countNonterminalNodes;
	}
	
	public int getIntermediateNodesCount() {
		return countIntermediateNodes;
	}
	
	public int getTokenNodesCount() {
		return countTerminalNodes;
	}
	
	public int getPackedNodesCount() {
		return countPackedNodes;
	}
	
	public int getAmbiguousNodesCount() {
		return countAmbiguousNodes;
	}
	
}
