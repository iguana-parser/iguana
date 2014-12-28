package org.jgll.util.visualization;

import org.jgll.regex.automaton.AutomatonVisitor;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;
import org.jgll.regex.automaton.VisitAction;


public class AutomatonToDot {
	
	private static final String NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String REJECT_NODE = "[shape=box, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String LOOKAHEAD_REJECT_NODE = "[shape=oval, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String LOOKAHEAD_ACCEPT_NODE = "[shape=diamond, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String FINAL_NODE = "[shape=doublecircle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	private static final String TRANSITION = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7, label=\"%s\"];";
	
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
				
				for(Transition transition : state.getTransitions()) {
					sb.append(String.format(TRANSITION, transition.toString().replace("\\", "\\\\")) + "\"state" + state.getId() + "\"" + "->" + "{\"state" + transition.getDestination().getId() + "\"}" + "\n");										
				}
			}
		});
		
		return sb.toString();
	}
	
	
}
