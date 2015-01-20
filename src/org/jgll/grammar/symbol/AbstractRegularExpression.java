package org.jgll.grammar.symbol;

import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	protected Automaton automaton;
	
	public AbstractRegularExpression(SymbolBuilder<? extends RegularExpression> builder) {
		super(builder);
	}
	
	@Override
	public Automaton getAutomaton() {
		if (automaton == null) {
			automaton = createAutomaton();
		}
		return automaton;
	}
	
	protected abstract Automaton createAutomaton();

}
