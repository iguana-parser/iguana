/*
 * Copyright (c) 2015, CWI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.util.visualization;

import static org.iguana.util.generator.GeneratorUtil.*;
import static org.iguana.util.visualization.GraphVizUtil.*;

import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.slot.BeforeLastTerminalTransition;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.ConditionalTransition;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.FirstAndLastTerminalTransition;
import org.iguana.grammar.slot.FirstTerminalTransition;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.LastSymbolAndEndGrammarSlot;
import org.iguana.grammar.slot.LastSymbolGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.util.generator.GeneratorUtil;

public class GrammarGraphToDot {

	public static String toDot(GrammarGraph g) {
		
		final StringBuilder sb = new StringBuilder();
		
		for (NonterminalGrammarSlot nonterminal : g.getNonterminals()) {
			toDot(nonterminal, sb);
		}
		
		return sb.toString();
	}
	
	private static void toDot(NonterminalGrammarSlot slot, StringBuilder sb) {
		sb.append("\"" + slot.getId() + "\"" + String.format(NONTERMINAL_SLOT, 
				escape(slot.getNonterminal().getParameters() != null? 
						String.format("%s(%s)", slot.getNonterminal().getName(), GeneratorUtil.listToString(slot.getNonterminal().getParameters(), ",")) 
						: slot.getNonterminal().getName())) + "\n");
		
		slot.getFirstSlots().forEach(s -> sb.append(EPSILON_TRANSITION + "\"" + slot.getId() + "\"" + "->" + "{\"" + s.getId() + "\"}" + "\n"));
		slot.getFirstSlots().forEach(s -> toDot(s, sb));
	}
	
	private static void toDot(GrammarSlot slot, StringBuilder sb) {
		if (slot instanceof LastSymbolAndEndGrammarSlot) {
			sb.append("\"" + slot.getId() + "\"" + String.format(LAST_SYMBOL_AND_END_SLOT, "") + "\n");
		} else if (slot instanceof LastSymbolGrammarSlot) {
			sb.append("\"" + slot.getId() + "\"" + String.format(LAST_SYMBOL_SLOT, "") + "\n");
		} else if (slot instanceof EndGrammarSlot) {
			sb.append("\"" + slot.getId() + "\"" + String.format(END_SLOT, "") + "\n");
		} else {
			sb.append("\"" + slot.getId() + "\"" + BODY_SLOT + "\n");
		}
		
		// TODO: improve this code
		slot.getTransitions().forEach(t -> { 
			if(t instanceof ConditionalTransition) {
				sb.append(String.format(TRANSITION, t.getLabel() + ", true") + "\"" + slot.getId() + "\"" + "->" + "{\"" + t.destination().getId() + "\"}" + "\n");
				
				BodyGrammarSlot ifFalse = ((ConditionalTransition) t).ifFalseDestination();
				
				if (ifFalse != null)
					sb.append(String.format(TRANSITION, t.getLabel() + ", false") + "\"" + slot.getId() + "\"" + "->" + "{\"" + ifFalse.getId() + "\"}" + "\n");
			} else if (t instanceof FirstAndLastTerminalTransition
						|| t instanceof FirstTerminalTransition
						|| t instanceof BeforeLastTerminalTransition) {
				sb.append(String.format(SPECIAL_TERMINAL_TRANSITION, t.getLabel()) + "\"" + slot.getId() + "\"" + "->" + "{\"" + t.destination().getId() + "\"}" + "\n");
			}
			else sb.append(String.format(TRANSITION, t.getLabel()) + "\"" + slot.getId() + "\"" + "->" + "{\"" + t.destination().getId() + "\"}" + "\n"); 
		});
		slot.getTransitions().forEach(t -> toDot(t.destination(), sb));
	}
	
}
