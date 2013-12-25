package org.jgll.regex;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class NFAOperations {
	
	public static DFA convert(NFA nfa) {
		
		Set<Set<State>> visitedStates = new HashSet<>();
		Deque<Set<State>> processList = new ArrayDeque<>();
		
		Set<State> initialState = new HashSet<>();
		initialState.add(nfa.getStartState());
		Set<State> newState = epsilonClosure(initialState);
		visitedStates.add(newState);
		processList.add(newState);
		
		BitSet characters = nfa.getCharacters();
		
		while(!processList.isEmpty()) {
			Set<State> stateSet = processList.poll();
			
			newState = epsilonClosure(move(stateSet, characters));
			
			if(!visitedStates.contains(newState)) {
				processList.add(newState);
			}
		}
		
		return null;
	}
	
	private static Set<State> epsilonClosure(Set<State> states) {
		Set<State> newStates = new HashSet<>();
		for(State state : states) {
			newStates.addAll(epsilonClosure(state));
		}
		return newStates;
	}
	
	private static Set<State> epsilonClosure(State state) {
		Set<State> newStates = new HashSet<>();
		newStates.add(state);
		for(Transition t : state.getTransitions()) {
			if(t.isEpsilonTransition()) {
				State destination = t.getDestination();
				newStates.add(destination);
				newStates.addAll(epsilonClosure(destination));
			}
		}	
		
		return newStates;
	}
	
	private static Set<State> move(Set<State> states, int[] starts) {
		
		Map<Set<State>, Transition> map = new HashMap<>();

		for(int i = 0; i < starts.length; i++) {
			
			Set<State> newStates = new HashSet<>();
			
			for(State state : states) {
				for(Transition transition : state.getTransitions()) {
					if(transition.getStart() == i) {
						newStates.add(transition.getDestination());
					}		
				}
			}
		}
		
		return newStates;
	}
	
	public static String toJavaCode(NFA nfa) {
		State startState = nfa.getStartState();
		StringBuilder sb = new StringBuilder();
		Map<State, Integer> visitedStates = new HashMap<>();
		visitedStates.put(startState, 1);
		toJavaCode(startState, sb, visitedStates);
		return sb.toString();
	}
	
	private static void toJavaCode(State state, StringBuilder sb, Map<State, Integer> visitedStates) {
		sb.append("State state" + visitedStates.get(state) + " = new State();\n");
		for(Transition transition : state.getTransitions()) {
			State destination = transition.getDestination();
			
			if(!visitedStates.keySet().contains(destination)) {
				visitedStates.put(destination, visitedStates.size() + 1);
				toJavaCode(destination, sb, visitedStates);
			}
			
			sb.append("state" + visitedStates.get(state) + ".addTransition(new Transition(" + transition.getStart() + 
					                                               ", " + transition.getEnd() + ", state" + visitedStates.get(destination) + ");\n");
		}
	}
	
	public static BitSet getCharacters(NFA nfa) {
		
		BitSet bitSet = new BitSet();
		
		State startState = nfa.getStartState();
		
		Set<State> visitedStates = new HashSet<>();
		Stack<State> stack = new Stack<>();
		
		stack.push(startState);
		visitedStates.add(startState);
		
		while(!stack.isEmpty()) {
			State state = stack.pop();
			
			for(Transition transition : state.getTransitions()) {
				bitSet.set(transition.getStart(), transition.getEnd());
				State destination = transition.getDestination();
				if(!visitedStates.contains(destination)) {
					visitedStates.add(destination);
					stack.push(destination);
				}
			}
		}
		
		return bitSet;
	}
	
	public static int[] getStarts(NFA nfa) {
		
		List<Integer> list = new ArrayList<>();
		
		State startState = nfa.getStartState();
		
		Set<State> visitedStates = new HashSet<>();
		Stack<State> stack = new Stack<>();
		
		stack.push(startState);
		visitedStates.add(startState);
		
		while(!stack.isEmpty()) {
			State state = stack.pop();
			
			for(Transition transition : state.getTransitions()) {
				list.add(transition.getStart());
				State destination = transition.getDestination();
				if(!visitedStates.contains(destination)) {
					visitedStates.add(destination);
					stack.push(destination);
				}
			}
		}
		
		int[] startsArray = new int[list.size()];
		int i = 0;
		for(int v : list) {
			startsArray[i++] = v;
		}
		return startsArray;
	}
	
}
