package org.jgll.grammar.symbol;

import org.jgll.regex.Automaton;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.State;


public class Epsilon extends AbstractSymbol implements RegularExpression {

	public static final int TOKEN_ID = 0;

	private static final long serialVersionUID = 1L;
	
	private static Epsilon instance;
	
	public static Epsilon getInstance() {
		if(instance == null) {
			instance = new Epsilon();
		}
		
		return instance;
	}
	
	private Epsilon() {
		super("epsilon");
	}

	@Override
	public Epsilon copy() {
		return this;
	}

    @Override
    public Automaton toAutomaton() {
        return createNFA();
    }
    
    private Automaton createNFA() {
    	State state = new State(true);
        return new Automaton(state);
    }

	@Override
	public boolean isNullable() {
		return true;
	}
	
}
