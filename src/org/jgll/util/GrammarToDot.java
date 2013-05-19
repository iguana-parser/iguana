package org.jgll.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.Alternate;
import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.NonterminalGrammarSlot;

import static org.jgll.util.GraphVizUtil.EDGE;
import static org.jgll.util.GraphVizUtil.NONTERMINAL_EDGE;
import static org.jgll.util.GraphVizUtil.SLOT_NODE;
import static org.jgll.util.GraphVizUtil.NONTERMINAL_NODE;;

public class GrammarToDot {
	
	public static String toDot(Grammar grammar) {
		StringBuilder sb = new StringBuilder();
		
		Set<HeadGrammarSlot> visitedHeads = new HashSet<>();
		
		Deque<HeadGrammarSlot> todoQueue = new ArrayDeque<>();
		
		for(HeadGrammarSlot head : grammar.getNonterminals()) {
			todoQueue.add(head);
			visitedHeads.add(head);
		}
		
		while(!todoQueue.isEmpty()) {
			HeadGrammarSlot head = todoQueue.poll();
			
			for(Alternate alternate : head.getAlternates()) {
				sb.append("\"" + head.getId() + "\"" + String.format(NONTERMINAL_NODE, head.getLabel()));
				
				GrammarSlot previousSlot = head;
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				while(currentSlot != null){
					if(currentSlot.isNonterminalSlot()) {
						HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) currentSlot).getNonterminal();
						if(!visitedHeads.contains(nonterminal)) {
							todoQueue.add(nonterminal);
							visitedHeads.add(nonterminal);
						}
						sb.append(NONTERMINAL_EDGE + "\"" + currentSlot.getId() + "\"" + "->" + "{\"" + nonterminal.getId() + "\"}" + "\n");
					}
					sb.append(EDGE + "\"" + previousSlot.getId() + "\"" + "->" + "{\"" + currentSlot.getId() + "\"}" + "\n");
					sb.append("\"" + currentSlot.getId() + "\"" + String.format(SLOT_NODE, currentSlot.getLabel()));
					previousSlot = currentSlot;
					currentSlot = currentSlot.next();
				}
				sb.append("\n");
			}

		}
				
		return sb.toString();
	}
	
}
