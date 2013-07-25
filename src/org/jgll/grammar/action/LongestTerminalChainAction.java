package org.jgll.grammar.action;

import org.jgll.grammar.GrammarVisitAction;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.KeywordGrammarSlot;
import org.jgll.grammar.LastGrammarSlot;
import org.jgll.grammar.NonterminalGrammarSlot;
import org.jgll.grammar.TerminalGrammarSlot;

public class LongestTerminalChainAction implements GrammarVisitAction {
	
	int longestTerminalChain = 0;
	
	int length = 0; // The length of the longest terminal chain for each rule
	
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
		return longestTerminalChain;
	}

}
