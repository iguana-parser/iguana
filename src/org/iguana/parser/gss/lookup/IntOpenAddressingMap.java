package org.iguana.parser.gss.lookup;

import iguana.utils.input.Input;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.CollectionsUtil;
import org.iguana.util.collections.IntHashMap;
import org.iguana.util.collections.OpenAddressingIntHashMap;

public class IntOpenAddressingMap extends AbstractNodeLookup {

	private IntHashMap<GSSNode> map = new OpenAddressingIntHashMap<>();
	
	@Override
	public void get(int i, GSSNodeCreator creator) {
		map.compute(i, (k, v) -> creator.create(v));
	}

	@Override
	public void reset(Input input) {
		super.reset(input);
		map = new OpenAddressingIntHashMap<>();
	}

	@Override
	public Iterable<GSSNode> getNodes() {
		return CollectionsUtil.concat(map.values(), super.map.values());
	}

	@Override
	public GSSNode get(NonterminalGrammarSlot slot, int i) {
		return map.computeIfAbsent(i, k -> new GSSNode(slot, i));
	}

}
