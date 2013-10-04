package org.jgll.grammar.symbol;

import java.util.Collection;

import org.jgll.grammar.condition.Condition;

import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;


public class RegularExpression extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private RunAutomaton automaton;

	private Sequence seq;
	
	public RegularExpression(Sequence seq) {
		this.seq = seq;
		this.automaton = new RunAutomaton(new RegExp(sequenceToBricsDFA()).toAutomaton());
	}
	
	public RunAutomaton getAutomaton() {
		return automaton;
	}
	
	public Sequence getSymbol() {
		return seq;
	}
	
	@Override
	public String getName() {
		return seq.toString();
	}

	@Override
	public Symbol addConditions(Collection<Condition> conditions) {
		return null;
	}
	
	public Terminal getFirstTerminal() {
		Symbol firstSymbol = seq.getSymbols().get(0);
		
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
	
	private String sequenceToBricsDFA() {
		
		StringBuilder sb = new StringBuilder();
		
		for(Symbol symbol : seq.getSymbols()) {
			if(symbol instanceof Terminal) {
				terminalToString((Terminal) symbol, sb);
			} 
			else if(symbol instanceof Plus) {
				terminalToString((Terminal) ((Plus) symbol).getSymbol(), sb);
				sb.append("+");
			} 
			else if(symbol instanceof Star) {
				terminalToString((Terminal) ((Star) symbol).getSymbol(), sb);
				sb.append("*");
			}
			else if(symbol instanceof Opt) {
				terminalToString((Terminal) ((Opt) symbol).getSymbol(), sb);
				sb.append("?");
			}
		}
		
		return sb.toString();
	}

	private void terminalToString(Terminal symbol, StringBuilder sb) {
		if(symbol instanceof Character) {
			sb.append((char)((Character) symbol).get());
		}
		else if (symbol instanceof CharacterClass) {
			sb.append(symbol.toString());
		}
	}

	
}
