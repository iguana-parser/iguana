package org.jgll.util;

import static org.jgll.util.GraphVizUtil.EDGE;
import static org.jgll.util.GraphVizUtil.NONTERMINAL_NODE;
import static org.jgll.util.GraphVizUtil.SLOT_NODE;
import static org.jgll.util.GraphVizUtil.NONTERMINAL_EDGE;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.Alternate;
import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.NonterminalGrammarSlot;

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
				sb.append("\"" + getId(head) + "\"" + String.format(NONTERMINAL_NODE, head.getLabel()));
				
				sb.append(EDGE + "\"" + getId(head) + "\"" + "->" + "{\"" + getId(alternate.getFirstSlot()) + "\"}" + "\n");
				
				BodyGrammarSlot previousSlot = null;
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				while(currentSlot != null){
					if(currentSlot.isNonterminalSlot()) {
						HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) currentSlot).getNonterminal();
						if(!visitedHeads.contains(nonterminal)) {
							todoQueue.add(nonterminal);
							visitedHeads.add(nonterminal);
						}
						sb.append(NONTERMINAL_EDGE + "\"" + getId(currentSlot) + "\"" + "->" + "{\"" + getId(nonterminal) + "\"}" + "\n");
					}
					
					if(previousSlot != null) {
						sb.append(EDGE + "\"" + getId(previousSlot) + "\"" + "->" + "{\"" + getId(currentSlot) + "\"}" + "\n");						
					}
					
					sb.append("\"" + getId(currentSlot) + "\"" + String.format(SLOT_NODE, currentSlot.getLabel()));
					previousSlot = currentSlot;
					currentSlot = currentSlot.next();
					
				}
//				sb.append(END_EDGE + "\"" + previousSlot.getId() + "\"" + "->" + "{\"" + ((BodyGrammarSlot) previousSlot).getHead().getId() + "\"}" + "\n");
				sb.append("\n");
			}

		}
				
		return sb.toString();
	}
	
	private static String getId(HeadGrammarSlot head) {
		return "n" + head.getId();
	}
	
	private static String getId(BodyGrammarSlot slot) {
		return "s" + slot.getId();
	}
	
}
