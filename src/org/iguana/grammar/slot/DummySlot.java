package org.iguana.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.iguana.parser.gss.lookup.HashMapNodeLookup;
import org.iguana.util.Input;


public class DummySlot extends BodyGrammarSlot {

	private static final DummySlot instance = new DummySlot();
	
	public static DummySlot getInstance() {
		return instance;
	}
	
	private DummySlot() {
		super(0, null, new HashMapNodeLookup().init(), null, null, Collections.emptySet());
	}

	@Override
	public String getConstructorCode() {
		return "new DummySlot()";
	}

	@Override
	public void reset(Input input) {
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}

	@Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}
	
	@Override
	public String toString() {
		return "$";
	}
	
}
