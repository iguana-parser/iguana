package org.jgll.grammar.symbol;

import java.util.BitSet;
import java.util.List;
import java.util.Set;


public class RegularExpressionUtil  {

	public static void getFirstTerminal(Set<Terminal> set, Symbol symbol) {
		
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
			for(Symbol s : list) {
				getFirstTerminal(set, s);
				if(!isRegexpNullable(s)) {
					break;
				}
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
	
	public static boolean isRegexpNullable(Symbol symbol) {
		
		if(symbol instanceof Terminal) {
			return false;
		}
		if(symbol instanceof Keyword) {
			return false;
		}
		else if(symbol instanceof Plus) {
			return false;
		}
		else if(symbol instanceof Star) {
			return true;
		}
		else if(symbol instanceof Opt) {
			return true;
		}
		else if(symbol instanceof Alt) {
			for(Symbol s : ((Alt) symbol).getSymbols()) {
				if(isRegexpNullable(s)) {
					return true;
				}
			}
			return false;
		}
		else if(symbol instanceof Group) {
			boolean nullable = false;
			for(Symbol s : ((Group) symbol).getSymbols()) {
				nullable |= isRegexpNullable(s);
			}
			return nullable;
		}
		else {
			throw new IllegalStateException("Unsupported regular symbol: " + symbol);			
		}
	}
	
	public static void symbolToString(Symbol symbol, StringBuilder sb) {
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

	public static void terminalToString(Terminal symbol, StringBuilder sb) {
		if(symbol instanceof Character) {
			sb.append((char)((Character) symbol).get());
		}
		else if (symbol instanceof CharacterClass) {
			sb.append(symbol.toString());
		}
	}

	public static BitSet get(Symbol symbol) {
		if(symbol instanceof Plus) {
			return get((Plus) symbol);
		} 
		else if (symbol instanceof Star) {
			return get((Star) symbol);
		}
		else if(symbol instanceof Group) {
			return get((Group) symbol);
		}
		else if(symbol instanceof Opt) {
			return get((Opt) symbol);
		}
		else if(symbol instanceof Alt) {
			return get((Alt) symbol);
		}
		else if(symbol instanceof Token) {
			return ((Token) symbol).asBitSet();
		}
		else {
			throw new RuntimeException("Unexpected regular expression type: " + symbol);
		}
	}
	
	public static BitSet get(Plus plus) {
		return get(plus.getSymbol());
	}
	
	public static BitSet get(Star star) {
		return get(star.getSymbol());
	}

	public static BitSet get(Opt opt) {
		return get(opt.getSymbol());
	}
	
	public static BitSet get(Alt alt) {
		BitSet set = new BitSet();
		for(Symbol s : alt.getSymbols()) {
			set.or(get(s));
		}
		return set;
	}
	
	public static BitSet get(Group group) {
		BitSet set = new BitSet();
		for(Symbol s : group.getSymbols()) {
			set.or(get(s));
			
			if(!isRegexpNullable(s)) {
				break;
			}
		}
		return set;
	}

}
