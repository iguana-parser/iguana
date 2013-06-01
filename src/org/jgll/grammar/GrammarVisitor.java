package org.jgll.grammar;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class GrammarVisitor {
	
	public static void visit(Grammar grammar, String nonterminalName, GrammarVisitAction action) {
		HeadGrammarSlot head = grammar.nameToNonterminals.get(nonterminalName);
		
		Set<HeadGrammarSlot> visitedHeads = new HashSet<>();
		
		Deque<HeadGrammarSlot> todoQueue = new ArrayDeque<>();
		
		todoQueue.add(head);
		visitedHeads.add(head);
		
		while(!todoQueue.isEmpty()) {
			HeadGrammarSlot next = todoQueue.poll();
			
			action.visit(next);
			
			for(Alternate alternate : next.getAlternates()) {
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				while(!currentSlot.isLastSlot()){
					if(currentSlot.isNonterminalSlot()) {
						action.visit((NonterminalGrammarSlot)currentSlot);
						
						HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) currentSlot).getNonterminal();
						if(nonterminal != null && !visitedHeads.contains(nonterminal)) {
							todoQueue.add(nonterminal);
							visitedHeads.add(nonterminal);
						}
					} else {
						action.visit((TerminalGrammarSlot)currentSlot);
					}
					currentSlot = currentSlot.next;
				}
				action.visit((LastGrammarSlot)currentSlot);
			}
		}
	}

}
