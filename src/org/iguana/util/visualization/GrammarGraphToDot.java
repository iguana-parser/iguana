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
import org.iguana.utils.visualization.DotGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.iguana.utils.string.StringUtil.listToString;
import static org.iguana.utils.visualization.DotGraph.newEdge;
import static org.iguana.utils.visualization.DotGraph.newNode;

public class GrammarGraphToDot {

	public static DotGraph toDot(GrammarGraph g, DotGraph.Direction direction) {
		DotGraph dotGraph = new DotGraph(direction);
		generate(dotGraph, g);
		return dotGraph;
	}

	public static DotGraph toDot(GrammarGraph g) {
		DotGraph dotGraph = new DotGraph();
		generate(dotGraph, g);
		return dotGraph;
	}

	private static void generate(DotGraph dotGraph, GrammarGraph g) {
		ids.clear();
		Set<Integer> visited = new HashSet<>();
		for (NonterminalGrammarSlot nonterminal : g.getNonterminalGrammarSlots()) {
			toDot(nonterminal, dotGraph, visited);
		}
	}

	private static void toDot(NonterminalGrammarSlot slot, DotGraph dotGraph, Set<Integer> visited) {
		int slotId = getId(slot);
		if (visited.contains(slotId)) {
			return;
		}

		visited.add(slotId);
		DotGraph.Node node = newNode(slotId);
		String label;
		if (slot.getNonterminal().getParameters() != null) {
			label = String.format("%s(%s)", slot.getNonterminal().getName(),
				listToString(slot.getNonterminal().getParameters(), ","));
		} else {
			label = slot.getNonterminal().getName();
		}
		node.setLabel(label);
		dotGraph.addNode(node);

		slot.getFirstSlots().forEach(s -> dotGraph.addEdge(newEdge(slotId, getId(s), "")));
		slot.getFirstSlots().forEach(s -> toDot(s, dotGraph, visited));
	}
	
	private static void toDot(BodyGrammarSlot slot, DotGraph dotGraph, Set<Integer> visited) {
		if (slot instanceof EndGrammarSlot) {
			dotGraph.addNode(newNode(getId(slot)).setShape(DotGraph.Shape.DOUBLE_CIRCLE));
		} else {
			dotGraph.addNode(newNode(getId(slot), slot.toString()).setShape(DotGraph.Shape.CIRCLE));

			// TODO: improve this code
			Transition t = slot.getOutTransition();
			if (t instanceof ConditionalTransition) {
				dotGraph.addEdge(newEdge(getId(slot), getId(t.destination()), t.getLabel()));

				BodyGrammarSlot ifFalse = ((ConditionalTransition) t).ifFalseDestination();
				if (ifFalse != null)
					dotGraph.addEdge(newEdge(getId(slot), getId(ifFalse), t.getLabel()));
			}
			else {
				dotGraph.addEdge(newEdge(getId(slot), getId(t.destination()), t.getLabel()));
			}

			toDot(t.destination(), dotGraph, visited);
		}
	}


    private static final Map<GrammarSlot, Integer> ids = new HashMap<>();

    private static int getId(GrammarSlot slot) {
        return ids.computeIfAbsent(slot, k -> ids.size() + 1);
    }
	
}
