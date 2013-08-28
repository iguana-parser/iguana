package org.jgll.grammar;

import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.KeywordGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;

public interface GrammarVisitAction {

	public void visit(HeadGrammarSlot head);
	
	public void visit(NonterminalGrammarSlot slot);
	
	public void visit(TerminalGrammarSlot slot);
	
	public void visit(LastGrammarSlot slot);
	
	public void visit(KeywordGrammarSlot slot);
	
}
