package org.jgll.grammar.symbol;

import org.jgll.regex.Automaton;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.State;

public class EOF extends AbstractSymbol implements RegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	public static final int TOKEN_ID = 1;

	private static EOF instance;
	
	public static EOF getInstance() {
		if(instance == null) {
			instance = new EOF();
		}
		return instance;
	}
	
	private EOF() {
		super("$");
	}
	
	@Override
	public Automaton toNFA() {
    	State state = new State(true);
        return new Automaton(state);
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public RegularExpression copy() {
		return this;
	}
	

}
