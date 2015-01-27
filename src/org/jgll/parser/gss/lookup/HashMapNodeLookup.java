package org.jgll.parser.gss.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.GSSNodeData;
import org.jgll.util.Input;

public class HashMapNodeLookup implements NodeLookup {

	private Map<Integer, GSSNode> map = new HashMap<>();
	
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

	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public <T> GSSNode getOrElseCreate(GrammarSlot slot, int i, GSSNodeData<T> data) {
		// FIXME:
		return null;
	}

	@Override
	public <T> GSSNode get(int i, GSSNodeData<T> data) {
		// FIXME:
		return null;
	}

}
