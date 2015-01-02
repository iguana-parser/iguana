package org.jgll.grammar.slot;

import java.util.HashMap;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Position;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.lookup.NodeAddedAction;


public class BodyGrammarSlot extends AbstractGrammarSlot {
	
	protected final Position position;
	
	private HashMap<IntermediateNode, IntermediateNode> intermediateNodes;

	public BodyGrammarSlot(Position position) {
		this.position = position;
		this.intermediateNodes = new HashMap<>();
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return new StringBuilder()
    	  .append("new BodyGrammarSlot(")
    	  .append(")").toString();
	}
	
	@Override
	public String toString() {
		return position.toString();
	}
	
	@Override
	public boolean isFirst() {
		return position.isFirst();
	}
	
	public IntermediateNode getIntermediateNode(IntermediateNode node, NodeAddedAction<IntermediateNode> action) {
		return intermediateNodes.computeIfAbsent(node, k -> { action.execute(k); return k.init(); });
	}
	
	public IntermediateNode findIntermediateNode(IntermediateNode node) {
		return intermediateNodes.get(node);
	}

	@Override
	public void reset() {
		intermediateNodes = new HashMap<>();
	}
	
}
