package org.jgll.parser.gss.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

public class HashMapNodeLookup implements NodeLookup {

	private Map<Integer, GSSNode> map = new HashMap<>();
	
	private static HashMapNodeLookup instance;
	
	public static HashMapNodeLookup getInstance() {
		if (instance == null) 
			instance = new HashMapNodeLookup();
		
		return instance;
	}

	private HashMapNodeLookup() {}
	
	@Override
	public GSSNode getOrElseCreate(GrammarSlot slot, int i) {
		return map.computeIfAbsent(i, k -> new GSSNode(slot, k));
	}

	@Override
	public GSSNode get(int i) {
		return map.get(i);
	}

	@Override
	public void reset(Input input) {
		map = new HashMap<>();
	}

}
