package org.jgll.regex;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
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
		
		State startState = null;
		
		while(!processList.isEmpty()) {
			Set<State> stateSet = processList.poll();
			State source = new State();
			
			if(startState == null) {
				startState = source;
			}
			
			Map<Set<State>, Integer> map = move(stateSet, characters);

			for(Set<State> s : map.keySet()) {
				if(s.isEmpty()) {
					continue;
				}
				newState = epsilonClosure(s);
				
				State destination = new State();
				source.addTransition(new Transition(map.get(s), destination));
				
				if(!visitedStates.contains(newState)) {
					processList.add(newState);
				}
			}
		}
		
		return new DFA(startState);
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
	
	private static Map<Set<State>, Integer> move(Set<State> states, BitSet bitSet) {
		
		Map<Set<State>, Integer> map = new HashMap<>();
		
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i+1)) {
			
			Set<State> newStates = new HashSet<>();

			for(State state : states) {
				for(Transition transition : state.getTransitions()) {
					if(transition.getStart() <= i && transition.getEnd() >= i) {
						newStates.add(transition.getDestination());
					}
				}
			}
			map.put(newStates, i);
		}
		
		return map;
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
				bitSet.set(transition.getStart(), transition.getEnd() + 1);
				State destination = transition.getDestination();
				if(!visitedStates.contains(destination)) {
					visitedStates.add(destination);
					stack.push(destination);
				}
			}
		}
		
		return bitSet;
	}
	
}
