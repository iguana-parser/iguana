package org.jgll.grammar.grammaraction;

import org.jgll.grammar.GrammarVisitAction;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.TerminalGrammarSlot;
import org.jgll.grammar.slot.KeywordGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;

public class LongestTerminalChainAction implements GrammarVisitAction {
	
	private int longestTerminalChain;
	
	private int length; // The length of the longest terminal chain for each rule
	
	@Override
	public void visit(KeywordGrammarSlot slot) {
		length = slot.getKeyword().size();
	}
	
	@Override
	public void visit(LastGrammarSlot slot) {
		if (length > longestTerminalChain) {
			longestTerminalChain = length;
		}
		length = 0;
	}
	
	@Override
	public void visit(TerminalGrammarSlot slot) {
		length++;
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

}
