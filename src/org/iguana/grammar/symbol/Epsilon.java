package org.jgll.grammar.symbol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.traversal.ISymbolVisitor;


public class Epsilon extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private static Epsilon instance;
	
	public static Epsilon getInstance() {
		if(instance == null) {
			instance = new Epsilon();
		}
		
		return instance;
	}
	
	private static SymbolBuilder<Epsilon> builder = 
			new SymbolBuilder<Epsilon>("epsilon") {
					@Override
					public Epsilon build() {
						return Epsilon.getInstance();
					}};
	
	private Epsilon() {
		super(builder);
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
        return Automaton.builder(state).build();
    }

	@Override
	public boolean isNullable() {
		return true;
	}
	
	@Override
	public Set<CharacterRange> getFirstSet() {
		HashSet<CharacterRange> firstSet = new HashSet<>();
		firstSet.add(CharacterRange.in(-1, -1));
		return firstSet;
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}

	@Override
	public String getConstructorCode() {
		return Epsilon.class.getSimpleName() + ".getInstance()";
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return builder;
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
