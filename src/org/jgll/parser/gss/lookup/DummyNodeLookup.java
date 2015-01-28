package org.jgll.parser.gss.lookup;

import java.util.Collections;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.GSSNodeData;
import org.jgll.util.Input;

public class DummyNodeLookup implements GSSNodeLookup {
	
	private final static DummyNodeLookup instance = new DummyNodeLookup();
	
	public static DummyNodeLookup getInstance() {
		return instance;
	}
	
	private DummyNodeLookup() {}

	@Override
	public GSSNode getOrElseCreate(GrammarSlot slot, int i) {
		return null;
	}

	@Override
	public GSSNode get(int i) {
		return null;
	}

	@Override
	public void reset(Input input) {
	}

	@Override
	public Iterable<GSSNode> getNodes() {
		return Collections.emptyList();
	}

	@Override
	public DummyNodeLookup init() {
		return this;
	}

	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public <T> GSSNode getOrElseCreate(GrammarSlot slot, int i, GSSNodeData<T> data) {
		// FIXME
		return null;
	}

	@Override
	public <T> GSSNode get(int i, GSSNodeData<T> data) {
		// TODO Auto-generated method stub
		return null;
	}

}
