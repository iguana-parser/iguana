package org.jgll.regex.automaton;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class AutomatonVisitor {

	public static void visit(Automaton nfa, VisitAction action) {
		visit(nfa.getStartState(), action);
	}
	
	public static void visit(State startState, VisitAction action) {
		Set<State> visitedStates = new HashSet<>();
		Stack<State> stack = new Stack<>();
		
		stack.push(startState);
		visitedStates.add(startState);
		
		while(!stack.isEmpty()) {
			State state = stack.pop();
			action.visit(state);
			
			for(Transition transition : state.getTransitions()) {
				State destination = transition.getDestination();
				if(!visitedStates.contains(destination)) {
					visitedStates.add(destination);
					stack.push(destination);
				}
			}
		}
	}
	
}
