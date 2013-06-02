package org.jgll.grammar;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class GrammarVisitor {
	
	private Set<HeadGrammarSlot> visitedHeads;
	private Deque<HeadGrammarSlot> todoQueue;
	private GrammarVisitAction action;
	
	public GrammarVisitor(GrammarVisitAction action) {
		this.action = action;
		visitedHeads = new HashSet<>();
		todoQueue = new ArrayDeque<>();		
	}

	public void visit(HeadGrammarSlot root) {
		todoQueue.add(root);
		visitedHeads.add(root);
		
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
