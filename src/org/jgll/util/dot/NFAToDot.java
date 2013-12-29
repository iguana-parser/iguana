package org.jgll.util.dot;

import org.jgll.regex.AutomatonVisitor;
import org.jgll.regex.State;
import org.jgll.regex.Transition;
import org.jgll.regex.VisitAction;


public class NFAToDot {
	
	public static final String NFA_NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String NFA_TRANSITION = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7, label=\"%s\"];";

	
	public static String toDot(State startState) {
		
		final StringBuilder sb = new StringBuilder();
		
		AutomatonVisitor.visit(startState, new VisitAction() {
			
			@Override
			public void visit(State state) {
				
				sb.append("\"state" + state.getId() + "\"" + String.format(NFA_NODE, state.getId()) + "\n");
				
				for(Transition transition : state.getTransitions()) {
					sb.append(String.format(NFA_TRANSITION, transition) + "\"state" + state.getId() + "\"" + "->" + "{\"state" + transition.getDestination().getId() + "\"}" + "\n");										
				}
				
			}
		});
		
		return sb.toString();
	}
	
	
}
