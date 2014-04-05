package org.jgll.util.dot;

import org.jgll.regex.automaton.AutomatonVisitor;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;
import org.jgll.regex.automaton.VisitAction;


public class AutomatonToDot {
	
	private static final String NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String REJECT_NODE = "[shape=squre, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String FINAL_NODE = "[shape=doublecircle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String TRANSITION = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7, label=\"%s\"];";
	

	
	public static String toDot(State startState) {
		
		final StringBuilder sb = new StringBuilder();
		
		AutomatonVisitor.visit(startState, new VisitAction() {
			
			@Override
			public void visit(State state) {
				
				if(state.isFinalState()) {
					sb.append("\"state" + state.getId() + "\"" + String.format(FINAL_NODE, state.getId()) + "\n");
				} else if (state.isRejectState()) {
					sb.append("\"state" + state.getId() + "\"" + String.format(REJECT_NODE, state.getId()) + "\n");
				} else {
					sb.append("\"state" + state.getId() + "\"" + String.format(NODE, state.getId()) + "\n");					
				}
				
				for(Transition transition : state.getTransitions()) {
					sb.append(String.format(TRANSITION, transition.toString().replace("\\", "\\\\")) + "\"state" + state.getId() + "\"" + "->" + "{\"state" + transition.getDestination().getId() + "\"}" + "\n");										
				}
				
			}
		});
		
		return sb.toString();
	}
	
	
}
