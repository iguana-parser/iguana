package org.jgll.grammar.symbol;

import java.util.Collection;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;

import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;


public class RegularExpression extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private RunAutomaton automaton;

	private List<? extends Symbol> symbols;
	
	public RegularExpression(List<? extends Symbol> symbols) {
		this.symbols = symbols;
		this.automaton = new RunAutomaton(new RegExp(sequenceToBricsDFA()).toAutomaton());
	}
	
	public RunAutomaton getAutomaton() {
		return automaton;
	}
	
	public List<? extends Symbol> getSymbols() {
		return symbols;
	}
	
	@Override
	public String getName() {
		return CollectionsUtil.listToString(symbols);
	}

	@Override
	public Symbol addConditions(Collection<Condition> conditions) {
		return null;
	}
	
	public Terminal getFirstTerminal() {
		Symbol firstSymbol = symbols.get(0);
		
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
		
		for(Symbol symbol : symbols) {
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
