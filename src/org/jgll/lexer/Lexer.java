package org.jgll.lexer;

import org.jgll.grammar.slot.TerminalGrammarSlot;



public interface Lexer {

	/**
	 * 
	 * Returns the length of the token found at the given input index
	 * for the given token id. 
	 * 
	 * @param inputIndex
	 * @param tokenID
	 * 
	 * @return -1 if no such tokens exists at the given input index.
	 */
	public int tokenLengthAt(int inputIndex, TerminalGrammarSlot t);
	
	public int charAt(int index);

}
