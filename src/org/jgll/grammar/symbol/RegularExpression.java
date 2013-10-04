package org.jgll.grammar.symbol;

import java.util.Collection;

import org.jgll.grammar.condition.Condition;

import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;


public class RegularExpression extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private RunAutomaton automaton;

	private Sequence symbol;
	
	public RegularExpression(Sequence symbol) {
		this.symbol = symbol;
		this.automaton = new RunAutomaton(new RegExp(symbol.toString()).toAutomaton());
	}
	
	public RunAutomaton getAutomaton() {
		return automaton;
	}
	
	public Sequence getSymbol() {
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
	
	public Terminal getFirstTerminal() {
		Symbol firstSymbol = symbol.getSymbols().get(0);
		
		if(firstSymbol instanceof Terminal) {
			return (Terminal) firstSymbol;
		}
		
		else if(firstSymbol instanceof Plus) {
			return (Terminal) (((Plus) firstSymbol).getSymbol());
		}
		
		else if(firstSymbol instanceof Star) {
			return (Terminal) (((Star) firstSymbol).getSymbol());
		}

		else if(firstSymbol instanceof Opt) {
			return (Terminal) (((Opt) firstSymbol).getSymbol());
		}

		throw new IllegalStateException();
	}
	
}
