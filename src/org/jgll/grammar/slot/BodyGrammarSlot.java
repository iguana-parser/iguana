package org.jgll.grammar.slot;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.lookup.NodeLookup;
import org.jgll.sppf.IntermediateNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.Key;


public class BodyGrammarSlot extends AbstractGrammarSlot {
	
	protected final Position position;
	
	private HashMap<Key, IntermediateNode> intermediateNodes;
	
	private final NodeLookup nodeLookup;

	public BodyGrammarSlot(Position position, NodeLookup nodeLookup) {
		this.position = position;
		this.nodeLookup = nodeLookup;
		this.intermediateNodes = new HashMap<>(1000);
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
	
	public IntermediateNode getIntermediateNode(Key key, Supplier<IntermediateNode> s, Consumer<IntermediateNode> c) {
		IntermediateNode val;
		if ((val = intermediateNodes.get(key)) == null) {
			val = s.get();
			c.accept(val);
			intermediateNodes.put(key, val);
		}
		return val;
	}
	
	public IntermediateNode findIntermediateNode(Key key) {
		return intermediateNodes.get(key);
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
