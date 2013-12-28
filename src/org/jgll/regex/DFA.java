package org.jgll.regex;

import org.jgll.util.Input;


public class DFA {
	
	private final int[][] transitionTable;
	
	private final boolean[] endStates;

	private int startStateId;

	public DFA(int[][] transitionTable, boolean[] endStates, int startStateId) {
		this.transitionTable = transitionTable;
		this.endStates = endStates;
		this.startStateId = startStateId;
	}

	public int run(Input input, int index) {
		int length = 0;

		int stateId = startStateId;
		
		while(true) {
			stateId = transitionTable[stateId][index];
			if(stateId == -1) {
				break;
			}

			if(endStates[stateId]) {
				length++;
			}
		}
		
		return length;
	}

}
