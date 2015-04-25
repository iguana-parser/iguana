package org.iguana.parser.gss.lookup;

import java.util.HashMap;
import java.util.Map;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.Input;

public class HashMapNodeLookup extends AbstractNodeLookup {

	private Map<Integer, GSSNode> map;
	
	@Override
	public GSSNode getOrElseCreate(GrammarSlot slot, int i) {
		GSSNode v;
		if ((v = map.get(i)) == null) {
			v = new GSSNode(slot, i);
			map.put(i, v);
		}
		return v;
	}

	@Override
	public GSSNode get(int i) {
		return map.get(i);
	}

	@Override
	public void reset(Input input) {
		super.reset(input);
		map = new HashMap<>();
	}

	@Override
	public Iterable<GSSNode> getNodes() {
		return map.values();
	}

	@Override
	public GSSNodeLookup init() {
		super.init();
		map = new HashMap<>();
		return this;
	}

	@Override
	public boolean isInitialized() {
		return map != null;
	}

}
