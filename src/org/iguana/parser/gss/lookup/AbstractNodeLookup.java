package org.iguana.parser.gss.lookup;

import java.util.HashMap;
import java.util.Map;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.util.Input;
import org.iguana.util.Tuple;

/**
 * 
 * @author Anastasia Izmaylova
 *
 */

public abstract class AbstractNodeLookup implements GSSNodeLookup {
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	private Map<Tuple<Integer, GSSNodeData<?>>, GSSNode> map;
	
	@Override
	public <T> GSSNode getOrElseCreate(GrammarSlot slot, int i, GSSNodeData<T> data) {
		Tuple<Integer, GSSNodeData<?>> elem = new Tuple<>(i, data);
		GSSNode v;
		if ((v = map.get(elem)) == null) {
			v = new org.iguana.datadependent.gss.GSSNode<T>(slot, i, data);
			map.put(elem, v);
		}
		return v;
	}

	@Override
	public <T> GSSNode get(int i, GSSNodeData<T> data) {
		return map == null? null : map.get(new Tuple<>(i, data));
	}
	
	@Override
	public void reset(Input input) {
		map = null;
	}
	
	@Override
	public GSSNodeLookup init() {
		map = new HashMap<>();
		return this;
	}

}
