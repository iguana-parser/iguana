/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
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

import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.slot.*;

import java.util.HashMap;
import java.util.Map;

import static iguana.utils.string.StringUtil.escape;
import static iguana.utils.string.StringUtil.listToString;
import static iguana.utils.visualization.GraphVizUtil.*;

public class GrammarGraphToDot {

	public static String toDot(GrammarGraph g) {
		ids.clear();
		final StringBuilder sb = new StringBuilder();
		
		for (NonterminalGrammarSlot nonterminal : g.getNonterminals()) {
			toDot(nonterminal, sb);
		}
		
		return sb.toString();
	}
	
	private static void toDot(NonterminalGrammarSlot slot, StringBuilder sb) {
		sb.append("\"" + getId(slot) + "\"" + String.format(NONTERMINAL_SLOT,
				escape(slot.getNonterminal().getParameters() != null? 
						String.format("%s(%s)", slot.getNonterminal().getName(), listToString(slot.getNonterminal().getParameters(), ","))
						: slot.getNonterminal().getName())) + "\n");
		
		slot.getFirstSlots().forEach(s -> sb.append(EPSILON_TRANSITION + "\"" + getId(slot) + "\"" + "->" + "{\"" + getId(s) + "\"}" + "\n"));
		slot.getFirstSlots().forEach(s -> toDot(s, sb));
	}
	
	private static void toDot(GrammarSlot slot, StringBuilder sb) {
		if (slot instanceof EndGrammarSlot) {
			sb.append("\"" + getId(slot) + "\"" + String.format(END_SLOT, "") + "\n");
		} else {
			sb.append("\"" + getId(slot) + "\"" + BODY_SLOT + "\n");
		}
		
		// TODO: improve this code
		slot.getTransitions().forEach(t -> { 
			if(t instanceof ConditionalTransition) {
				sb.append(String.format(TRANSITION, t.getLabel() + ", true") + "\"" + getId(slot) + "\"" + "->" + "{\"" + getId(t.destination()) + "\"}" + "\n");
				
				BodyGrammarSlot ifFalse = ((ConditionalTransition) t).ifFalseDestination();
				
				if (ifFalse != null)
					sb.append(String.format(TRANSITION, t.getLabel() + ", false") + "\"" + getId(slot) + "\"" + "->" + "{\"" + getId(ifFalse) + "\"}" + "\n");
			}
			else sb.append(String.format(TRANSITION, t.getLabel()) + "\"" + getId(slot) + "\"" + "->" + "{\"" + getId(t.destination()) + "\"}" + "\n");
		});
		slot.getTransitions().forEach(t -> toDot(t.destination(), sb));
	}


    private static Map<GrammarSlot, Integer> ids = new HashMap<>();

    private static int getId(GrammarSlot slot) {
        return ids.computeIfAbsent(slot, k -> ids.size() + 1);
    }
	
}
