package org.iguana.parser.gss.lookup;

import java.util.Collections;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.util.Input;

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

	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public <T> GSSNode getOrElseCreate(GrammarSlot slot, int i, GSSNodeData<T> data) {
		return null;
	}

	@Override
	public <T> GSSNode get(int i, GSSNodeData<T> data) {
		return null;
	}

}
