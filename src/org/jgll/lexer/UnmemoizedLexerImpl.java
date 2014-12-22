package org.jgll.lexer;

import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.util.Input;

/**
 * 
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class UnmemoizedLexerImpl implements Lexer {
	
	private Input input;

	public UnmemoizedLexerImpl(Input input) {
		this.input = input;
	}
	
	@Override
	public int tokenLengthAt(int inputIndex, TerminalGrammarSlot t) {
		return t.getRegularExpression().getAutomaton().getRunnableAutomaton().match(input, inputIndex);
	}

	@Override
	public int charAt(int index) {
		return input.charAt(index);
	}

}
