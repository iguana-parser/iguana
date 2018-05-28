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

import iguana.regex.automaton.AutomatonVisitor;
import iguana.regex.automaton.State;
import iguana.regex.automaton.Transition;
import iguana.regex.automaton.VisitAction;


public class AutomatonToDot {
	
	private static final String NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String REJECT_NODE = "[shape=box, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String LOOKAHEAD_REJECT_NODE = "[shape=oval, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String LOOKAHEAD_ACCEPT_NODE = "[shape=diamond, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String FINAL_NODE = "[shape=doublecircle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String TRANSITION = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7, label=\"%s\"];";
	private static final String EPSILON_TRANSITION = "edge [color=black, style=dashed, penwidth=0.5, arrowsize=0.7, label=\"&epsilon;\"];";
	
	public static String toDot(State startState) {
		
		final StringBuilder sb = new StringBuilder();
		
		AutomatonVisitor.visit(startState, new VisitAction() {
			
			@Override
			public void visit(State state) {
				
				String s = null;
				
				switch (state.getStateType()) {
					case FINAL:
						s = FINAL_NODE;						
						break;

					case REJECT:
						s = REJECT_NODE;
						break;
						
					case LOOKAHEAD_ACCEPT:
						s = LOOKAHEAD_ACCEPT_NODE;
						break;
						
					case LOOKAHEAD_REJECT:
						s = LOOKAHEAD_REJECT_NODE;
						break;
						
					case NORMAL:
						s = NODE;
						break;
						
				}
				
				sb.append("\"state" + state.getId() + "\"" + String.format(s, state.toString()) + "\n");					
				
				for (Transition transition : state.getTransitions()) {
					if (transition.isEpsilonTransition()) {
						sb.append(EPSILON_TRANSITION + "\"state" + state.getId() + "\"" + "->" + "{\"state" + transition.getDestination().getId() + "\"}" + "\n");
					} else {
						sb.append(String.format(TRANSITION, transition.toString().replace("\\", "\\\\")) + "\"state" + state.getId() + "\"" + "->" + "{\"state" + transition.getDestination().getId() + "\"}" + "\n");
					}
				}
				
			}
		});
		
		return sb.toString();
	}
}
