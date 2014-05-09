package org.jgll.util.dot;

import static org.jgll.util.dot.GraphVizUtil.EDGE;
import static org.jgll.util.dot.GraphVizUtil.END_EDGE;
import static org.jgll.util.dot.GraphVizUtil.NONTERMINAL_EDGE;
import static org.jgll.util.dot.GraphVizUtil.NONTERMINAL_NODE;
import static org.jgll.util.dot.GraphVizUtil.SLOT_NODE;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;

public class GrammarToDot {
	
	public static String toDot(GrammarGraph grammar) {
		StringBuilder sb = new StringBuilder();
		
		Set<HeadGrammarSlot> visitedHeads = new HashSet<>();
		
		Deque<HeadGrammarSlot> todoQueue = new ArrayDeque<>();
		
		for(HeadGrammarSlot head : grammar.getNonterminals()) {
			todoQueue.add(head);
			visitedHeads.add(head);
		}
		
		while(!todoQueue.isEmpty()) {
			HeadGrammarSlot head = todoQueue.poll();
			
			for(BodyGrammarSlot slot : head.getFirstSlots()) {
				sb.append("\"" + getId(head) + "\"" + String.format(NONTERMINAL_NODE, head.getNonterminal().getName()));
				
				sb.append(EDGE + "\"" + getId(head) + "\"" + "->" + "{\"" + getId(slot) + "\"}" + "\n");
				
				BodyGrammarSlot previousSlot = null;
				BodyGrammarSlot currentSlot = slot;
				while(currentSlot != null){
					if(currentSlot instanceof NonterminalGrammarSlot) {
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
					
					sb.append("\"" + getId(currentSlot) + "\"" + String.format(SLOT_NODE, currentSlot));
					previousSlot = currentSlot;
					currentSlot = currentSlot.next();
					
				}
				sb.append(END_EDGE + "\"" + getId(previousSlot) + "\"" + "->" + "{\"" + getId(((LastGrammarSlot) previousSlot).getHead()) + "\"}" + "\n");
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
