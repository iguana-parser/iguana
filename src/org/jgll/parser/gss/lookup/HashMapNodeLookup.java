package org.jgll.parser.gss.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

public class HashMapNodeLookup implements GSSNodeLookup {

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
		map = new HashMap<>();
	}

	@Override
	public Iterable<GSSNode> getNodes() {
		return map.values();
	}

	@Override
	public GSSNodeLookup init() {
		map = new HashMap<>();
		return this;
	}

	@Override
	public boolean isInitialized() {
		return map != null;
	}

}
