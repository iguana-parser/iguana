package org.jgll.grammar.symbol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;


public class Epsilon extends AbstractRegularExpression {

	public static final TerminalGrammarSlot TOKEN_ID = new TerminalGrammarSlot(Epsilon.getInstance());

	private static final long serialVersionUID = 1L;
	
	private static Epsilon instance;
	
	public static Epsilon getInstance() {
		if(instance == null) {
			instance = new Epsilon();
		}
		
		return instance;
	}
	
	private Epsilon() {
		super("epsilon", null, Collections.<Condition>emptySet(), null);
	}
	
	private Object readResolve()  {
	    return instance;
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	    ois.defaultReadObject();
	    instance = this;
	}

	@Override
    protected Automaton createAutomaton() {
    	State state = new State(StateType.FINAL);
        return new Automaton(state, name);
    }

	@Override
	public boolean isNullable() {
		return true;
	}
	
	@Override
	public Set<Range> getFirstSet() {
		HashSet<Range> firstSet = new HashSet<>();
		firstSet.add(Range.in(-1, -1));
		return firstSet;
	}
	
	@Override
	public Set<Range> getNotFollowSet() {
		return Collections.emptySet();
	}

	@Override
	public SymbolBuilder<Epsilon> builder() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "Epsilon.getInstance()";
	}
	
}
