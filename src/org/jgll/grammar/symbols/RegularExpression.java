package org.jgll.grammar.symbols;

import java.util.Collection;

import org.jgll.grammar.condition.Condition;

import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;


public class RegularExpression extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private RunAutomaton automaton;
	private String exp;
	
	public RegularExpression(String exp) {
		this.exp = exp;
		this.automaton = new RunAutomaton(new RegExp(exp).toAutomaton());
	}
	
	public RunAutomaton getAutomaton() {
		return automaton;
	}
	
	@Override
	public String getName() {
		return exp;
	}

	@Override
	public Symbol addConditions(Collection<Condition> conditions) {
		return null;
	}

}
