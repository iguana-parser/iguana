package org.jgll.grammar.grammaraction;

import org.jgll.grammar.GrammarVisitAction;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;

public class LongestTerminalChainAction implements GrammarVisitAction {
	
	private int longestTerminalChain;
	
	private int length; // The length of the longest terminal chain for each rule
	
	@Override
	public void visit(LastGrammarSlot slot) {
		if (length > longestTerminalChain) {
			longestTerminalChain = length;
		}
		length = 0;
	}
		
	@Override
	public void visit(NonterminalGrammarSlot slot) {
		if (length > longestTerminalChain) {
			longestTerminalChain = length;
		}
		length = 0;
	}
	
	@Override
	public void visit(HeadGrammarSlot head) {}
	
	public int getLongestTerminalChain() {
		return longestTerminalChain == 0 ? 1 : longestTerminalChain;
	}

	@Override
	public void visit(TokenGrammarSlot slot) {
		// TODO Auto-generated method stub
	}

}
