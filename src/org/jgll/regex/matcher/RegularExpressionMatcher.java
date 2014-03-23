package org.jgll.regex.matcher;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.jgll.regex.State;
import org.jgll.regex.StateAction;
import org.jgll.util.Input;

public class RegularExpressionMatcher implements Matcher, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected final int[][] transitionTable;
	
	protected final boolean[] endStates;
	
	protected final Set<StateAction>[] matchActions;

	protected final int startStateId;
	
	protected int id;
	
	protected int mode = LONGEST_MATCH;

	private Transitions transitions;

	public RegularExpressionMatcher(int[][] transitionTable, 
						   boolean[] endStates, 
						   int startStateId,
						   Transitions transitions,
						   Set<StateAction>[] matchActions) {
		
		this.transitionTable = transitionTable;
		this.endStates = endStates;
		this.startStateId = startStateId;
		this.transitions = transitions;
		this.matchActions = matchActions;
	}

	@Override
	public boolean match(Input input) {
		return match(input, 0) == input.length() - 1;
	}
	
	@Override
	public boolean match(Input input, int start, int end) {
		return match(input, start) == end - start;
	}
	
	@Override
	public int match(Input input, int inputIndex) {
		
		int length = 0;

		int stateId = startStateId;
		
		int maximumMatched = -1;
		
		// If the start state is an accepting state, we can always match a string with length 0.
		if(endStates[stateId]) {
			maximumMatched = 0;

			// Handling epsilon matching the empty input
			if(input.isEmpty()) {
				executeActions(stateId, maximumMatched);
				return maximumMatched;
			}

			if(mode == SHORTEST_MATCH) {
				executeActions(stateId, maximumMatched);
			}
			
			// If the input index is EOF and the current state is an end state.
			if(input.charAt(inputIndex) == 0) {
				executeActions(stateId, 0);
				return 0;
			}
		}		
		
		// Handling the EOF character
		if(input.charAt(inputIndex) == 0) {
			int transitionId = transitions.getTransitionId(0);

			if(transitionId == -1) {
				return -1;
			}

			stateId = transitionTable[stateId][transitionId];
			
			if(stateId == -1) {
				return -1;
			}
			
			if(endStates[stateId]) {
				executeActions(stateId, 0);
				return 0;
			}
			
			return -1;
		}
		
		for(int i = inputIndex; i < input.length(); i++) {
			int transitionId = transitions.getTransitionId(input.charAt(i));
			
			if(transitionId == -1) {
				break;
			}
			
			stateId = transitionTable[stateId][transitionId];
			length++;
			
			if(stateId == -1) {
				break;
			}
			
			if(endStates[stateId]) {
				maximumMatched = length;
				executeActions(stateId, maximumMatched);
				if(mode == SHORTEST_MATCH) {
					break;
				}
			}
		}
		
		return maximumMatched;
	}
	
	private void executeActions(int previousId, int maximumMatched) {
		for(StateAction action : matchActions[previousId]) {
			action.execute(maximumMatched, previousId);
		}
	}
	
	@Override
	public int matchBackwards(Input input, int inputIndex) {
		int length = 0;

		int stateId = startStateId;
		
		int maximumMatched = -1;
		
		if(input.isEmpty() && endStates[stateId]) {
			return 0;
		}
		
		for(int i = inputIndex; i >= 0; i--) {
			int transitionId = transitions.getTransitionId(input.charAt(i));
			
			if(transitionId == -1) {
				break;
			}
			
			stateId = transitionTable[stateId][transitionId];
			length++;
			
			if(stateId == -1) {
				break;
			}
			
			if(endStates[stateId]) {
				maximumMatched = length;
			}
		}
		
		return maximumMatched;
	}
	
	@Override
	public Matcher setMode(int mode) {
		this.mode = mode; 
		return this;
	}
	
	@Override
	public void addStateAction(State state, StateAction action) {
		Set<StateAction> set = matchActions[state.getId()];
		if(set == null) {
			set = new HashSet<>();
		}
		set.add(action);
	}

	@Override
	public Matcher copy() {
		return new RegularExpressionMatcher(transitionTable, endStates, startStateId, transitions, matchActions);
	}
	
}
