package org.jgll.grammar.slot;

import java.util.HashMap;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.lookup.NodeLookup;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.lookup.NodeAddedAction;
import org.jgll.util.Input;


public class BodyGrammarSlot extends AbstractGrammarSlot {
	
	protected final Position position;
	
	private HashMap<IntermediateNode, IntermediateNode> intermediateNodes;
	
	private final NodeLookup nodeLookup;

	public BodyGrammarSlot(Position position, NodeLookup nodeLookup) {
		this.position = position;
		this.intermediateNodes = new HashMap<>();
		this.nodeLookup = nodeLookup;
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
	public GSSNode getGSSNode(int inputIndex) {
		return nodeLookup.getOrElseCreate(this, inputIndex);
	}
	
	@Override
	public GSSNode hasGSSNode(int inputIndex) { 
		return nodeLookup.get(inputIndex);
	}

	@Override
	public void reset(Input input) {
		intermediateNodes = new HashMap<>();
		nodeLookup.reset(input);
	}
}
