package org.jgll.grammar.symbol;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		conditions.addAll(symbols.get(0).getConditions());
		if(symbols.size() > 0) {
			conditions.addAll(symbols.get(symbols.size() - 1).getConditions());
		}
		System.out.println(toBricsDFA());
		this.automaton = new RunAutomaton(new RegExp(toBricsDFA()).toAutomaton());
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
	public RegularExpression addConditions(Collection<Condition> conditions) {
		RegularExpression regex = new RegularExpression(this.symbols);
		regex.conditions.addAll(this.conditions);
		regex.conditions.addAll(conditions);
		return regex;
	}
	
	public Set<Terminal> getFirstTerminal() {
		Symbol firstSymbol = symbols.get(0);
		
		Set<Terminal> set = new HashSet<>();
		
		getFirstTerminal(set, firstSymbol);

		return set;
	}
	
	private void getFirstTerminal(Set<Terminal> set, Symbol symbol) {
		
		if(symbol instanceof Terminal) {
			set.add((Terminal) symbol);
		}
		
		else if(symbol instanceof Plus) {
			getFirstTerminal(set, (((Plus) symbol).getSymbol()));
		}
		
		else if(symbol instanceof Star) {
			getFirstTerminal(set, (((Star) symbol).getSymbol()));
		}

		else if(symbol instanceof Opt) {
			getFirstTerminal(set, (((Opt) symbol).getSymbol()));
		}
		
		else if(symbol instanceof Group) {
			List<? extends Symbol> list = ((Group) symbol).getSymbols();
			if(list.size() > 0) {
				getFirstTerminal(set, list.get(0));
			}
		}
		
		else if(symbol instanceof Alt) {
			for(Symbol s : ((Alt) symbol).getSymbols()) {
				getFirstTerminal(set, s);
			}
		} 

		else {
			throw new IllegalStateException("Unsupported regular symbol: " + symbol);			
		}

	}
	
	private String toBricsDFA() {
		
		StringBuilder sb = new StringBuilder();
		
		for(Symbol symbol : symbols) {
			symbolToString(symbol, sb);
		}
		
		return sb.toString();
	}
	
	private void symbolToString(Symbol symbol, StringBuilder sb) {
		if(symbol instanceof Terminal) {
			terminalToString((Terminal) symbol, sb);
		} 
		else if(symbol instanceof Plus) {
			symbolToString(((Plus) symbol).getSymbol(), sb);
			sb.append("+");
		} 
		else if(symbol instanceof Star) {
			symbolToString(((Star) symbol).getSymbol(), sb);
			sb.append("*");
		}
		else if(symbol instanceof Opt) {
			symbolToString(((Opt) symbol).getSymbol(), sb);
			sb.append("?");
		}
		else if(symbol instanceof Group) {
			sb.append("(");
			for(Symbol s : ((Group)symbol).getSymbols()) {
				symbolToString(s, sb);
			}
			sb.append(")");
		}
		else if(symbol instanceof Alt) {
			sb.append("(");
			for(Symbol s : ((Alt)symbol).getSymbols()) {
				symbolToString(s, sb);
				sb.append("|");
			}
			sb.delete(sb.length() - 1, sb.length());
			sb.append(")");
		}
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
