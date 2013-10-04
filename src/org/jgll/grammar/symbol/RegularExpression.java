package org.jgll.grammar.symbol;

import java.util.Collection;

import org.jgll.grammar.condition.Condition;

import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;


public class RegularExpression extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private RunAutomaton automaton;

	private Symbol symbol;
	
	public RegularExpression(Symbol symbol) {
		this.symbol = symbol;
		this.automaton = new RunAutomaton(new RegExp(symbol.toString()).toAutomaton());
	}
	
	public RunAutomaton getAutomaton() {
		return automaton;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	@Override
	public String getName() {
		return symbol.toString();
	}

	@Override
	public Symbol addConditions(Collection<Condition> conditions) {
		return null;
	}

}
