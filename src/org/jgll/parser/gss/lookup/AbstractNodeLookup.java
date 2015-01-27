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

public abstract class AbstractNodeLookup implements NodeLookup {
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	private Map<Tuple<Integer, GSSNodeData<?>>, GSSNode> map;
	
	@Override
	public <T> GSSNode getOrElseCreate(GrammarSlot slot, int i, GSSNodeData<T> data) {
		GSSNode v;
		if ((v = map.get(data)) == null) {
			// FIXME:
			v = new GSSNode(slot, i);
			if (map == null) {
				map = new HashMap<>();
			}
			map.put(new Tuple<Integer, GSSNodeData<?>>(i, data), v);
		}
		return v;
	}

	@Override
	public <T> GSSNode get(int i, GSSNodeData<T> data) {
		return map == null? null : map.get(new Tuple<Integer, GSSNodeData<?>>(i, data));
	}

}
