package org.jgll.parser.gss.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.GSSNodeData;
import org.jgll.util.Tuple;

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
			v = new org.jgll.datadependent.gss.GSSNode<T>(slot, i, data);
			if (map == null) {
				map = new HashMap<>();
			}
			map.put(elem, v);
		}
		return v;
	}

	@Override
	public <T> GSSNode get(int i, GSSNodeData<T> data) {
		return map == null? null : map.get(new Tuple<>(i, data));
	}

}
