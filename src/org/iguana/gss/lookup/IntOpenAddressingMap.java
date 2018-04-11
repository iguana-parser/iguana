package org.iguana.gss.lookup;

import iguana.utils.collections.CollectionsUtil;
import iguana.utils.collections.IntHashMap;
import iguana.utils.collections.OpenAddressingIntHashMap;
import iguana.utils.input.Input;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.gss.GSSNode;
import org.iguana.result.Result;

public class IntOpenAddressingMap<T extends Result> extends AbstractNodeLookup<T> {

	private IntHashMap<GSSNode<T>> map = new OpenAddressingIntHashMap<>();

	@Override
	public void get(int i, GSSNodeCreator<T> creator) {
		map.compute(i, (k, v) -> creator.create(v));
	}

	@Override
	public GSSNode<T> get(int i) {
		return map.get(i);
	}

	@Override
	public void put(int i, GSSNode<T> gssNode) {
		map.put(i, gssNode);
	}

	@Override
	public void reset(Input input) {
		super.reset(input);
		map = new OpenAddressingIntHashMap<>();
	}

	@Override
	public Iterable<GSSNode<T>> getNodes() {
		return CollectionsUtil.concat(map.values(), super.map.values());
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public GSSNode<T> get(NonterminalGrammarSlot slot, int i) {
		return map.computeIfAbsent(i, k -> new GSSNode<>(slot, i));
	}

}
