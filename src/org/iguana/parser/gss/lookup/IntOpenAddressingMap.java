package org.iguana.parser.gss.lookup;

import iguana.utils.collections.CollectionsUtil;
import iguana.utils.collections.IntHashMap;
import iguana.utils.collections.OpenAddressingIntHashMap;
import iguana.utils.input.Input;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.result.ResultOps;
import org.iguana.parser.gss.GSSNode;

public class IntOpenAddressingMap<T> extends AbstractNodeLookup<T> {

	private IntHashMap<GSSNode<T>> map = new OpenAddressingIntHashMap<>();

	public IntOpenAddressingMap(ResultOps<T> ops) {
		super(ops);
	}

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
	public GSSNode<T> get(NonterminalGrammarSlot<T> slot, int i) {
		return map.computeIfAbsent(i, k -> new GSSNode<>(slot, i, ops));
	}

}
