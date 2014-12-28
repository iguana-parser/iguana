package org.jgll.util.visualization;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.EndGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;

import static org.jgll.util.visualization.GraphVizUtil.*;
import static org.jgll.util.generator.GeneratorUtil.*;

public class GrammarGraphToDot {

	public static String toDot(GrammarGraph g) {
		
		final StringBuilder sb = new StringBuilder();
		
		for (NonterminalGrammarSlot nonterminal : g.getNonterminals()) {
			toDot(nonterminal, sb, g.getRegistry());
		}
		
		return sb.toString();
	}
	
	private static void toDot(GrammarSlot slot, StringBuilder sb, GrammarSlotRegistry r) {

		if (slot instanceof NonterminalGrammarSlot) {
			sb.append("\"" + r.getId(slot) + "\"" + String.format(NONTERMINAL_NODE, escape(slot.toString())) + "\n");			
		} else if (slot instanceof EndGrammarSlot) {
			sb.append("\"" + r.getId(slot) + "\"" + String.format(END_NODE, escape(slot.toString())) + "\n");
		}
		
		slot.getTransitions().forEach(t -> sb.append(sb.append(String.format(TRANSITION, t.toString()) + "\"" + r.getId(slot) + "\"" + "->" + "{\"" + r.getId(t.destination()) + "\"}" + "\n")));
		slot.getTransitions().forEach(t -> toDot(t.destination(), sb, r));
	}
	
}
