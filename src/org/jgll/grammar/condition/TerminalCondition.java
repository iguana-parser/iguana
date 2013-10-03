package org.jgll.grammar.condition;

import org.jgll.grammar.symbols.Terminal;

/**
 * 
 * Conditions relating to the character classes. 
 * For example, Id !>> [0-9] or Id \ [a-z]
 * 
 * All more complicated patterns should be modeled using 
 * context-free conditions.
 * 
 * @author Ali Afroozeh
 *
 */
public class TerminalCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private Terminal terminal;
	
	public TerminalCondition(ConditionType type, Terminal terminal) {
		super(type);
		this.terminal = terminal;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}
	
	@Override
	public String toString() {
		return type.toString() + " " + terminal;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof TerminalCondition)) {
			return false;
		}
		
		TerminalCondition other = (TerminalCondition) obj;
		
		return type == other.type && terminal.equals(other.terminal);
	}

}
