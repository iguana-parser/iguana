package org.iguana.grammar.symbol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.State;
import org.iguana.regex.automaton.StateType;
import org.iguana.regex.automaton.Transition;
import org.iguana.traversal.ISymbolVisitor;

public class EOF extends AbstractRegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	public static final int TOKEN_ID = 1;
	
	public static int VALUE = -1;
	
	private static EOF instance;
	
	public static EOF getInstance() {
		if(instance == null) {
			instance = new EOF();
		}
		return instance;
	}
	
	private EOF() {
		super(new SymbolBuilder<EOF>("$") {
			@Override
			public EOF build() {
				return EOF.getInstance();
			}
		});
	}
	
	private Object readResolve()  {
	    return instance;
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	    ois.defaultReadObject();
	    instance = this;
	}
	
	protected Automaton createAutomaton() {
    	State startState = new State();
    	State endState = new State(StateType.FINAL);
    	startState.addTransition(new Transition(VALUE, endState));
        return Automaton.builder(startState).build();		
	}

	@Override
	public boolean isNullable() {
		return false;
	}
	
	@Override
	public Set<CharacterRange> getFirstSet() {
		Set<CharacterRange> firstSet = new HashSet<>();
		firstSet.add(CharacterRange.in(VALUE, VALUE));
		return firstSet;
	}

	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}

	@Override
	public String getConstructorCode() {
		return "EOF.getInstance()";
	}
	
    @Override
    public SymbolBuilder<? extends Symbol> copyBuilder() {
        throw new UnsupportedOperationException();
    }

	@Override
	public String getPattern() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}
