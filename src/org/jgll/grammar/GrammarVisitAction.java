package org.jgll.grammar;

public interface GrammarVisitAction {

	public void visit(HeadGrammarSlot head);
	
	public void visit(NonterminalGrammarSlot slot);
	
	public void visit(TerminalGrammarSlot slot);
	
	public void visit(LastGrammarSlot slot);
}
