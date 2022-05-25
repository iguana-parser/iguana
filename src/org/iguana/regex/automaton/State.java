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

package org.iguana.regex.automaton;

import org.iguana.regex.CharRange;
import org.iguana.regex.RegularExpression;

import java.io.Serializable;
import java.util.*;

public class State implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Set<Transition> transitions;
	
	private final Map<CharRange, State> reachableStates;
	
	private final Set<State> epsilonSates;
			
	/**
	 * The set of regular expressions whose final state is this state.
	 */
	private Set<RegularExpression> regularExpressions;
	
	private StateType stateType = StateType.NORMAL;
	
	private int id;
	
	public State() {
		this(StateType.NORMAL);
	}
	
	public State(StateType stateType) {
		this.transitions = new HashSet<>();
		this.reachableStates = new HashMap<>();
		this.stateType = stateType;
		this.regularExpressions = new HashSet<>();
		this.epsilonSates = new HashSet<>();
	}
	
	public Set<Transition> getTransitions() {
		return transitions;
	}
	
	public State getState(CharRange r) {
		return reachableStates.get(r);
	}

	public boolean hasTransition(CharRange r) {
		return reachableStates.get(r) != null;
	}

	public StateType getStateType() {
		return stateType;
	}
	
	public boolean isFinalState() {
		return stateType == StateType.FINAL;
	}
	
	public void setStateType(StateType stateType) {
		this.stateType = stateType;
	}
	
	public void addEpsilonTransition(State dest) {
		transitions.add(new Transition(-1, dest));
		epsilonSates.add(dest);
	}
	
	public void addTransitions(Iterable<Transition> transitions) {
		for (Transition t : transitions) {
			addTransition(t);
		}
	}
	
	public void addTransition(Transition transition) {
		if (transition.isEpsilonTransition())
			epsilonSates.add(transition.getDestination());
		
		if (transitions.add(transition)) {
			reachableStates.put(transition.getRange(), transition.getDestination());
		}
	}
	
	public void removeTransition(Transition transition) {
		transitions.remove(transition);
	}
	
	public void removeTransitions(Collection<Transition> c) {
		transitions.removeAll(c);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void clearRegularExpressions() {
		regularExpressions.clear();
	}
	
	public Set<RegularExpression> getRegularExpressions() {
		return regularExpressions;
	}
	
	public Set<State> getEpsilonSates() {
		return epsilonSates;
	}
	
	public State addRegularExpression(RegularExpression regex) {
		regularExpressions.add(regex);
		return this;
	}
	
	public State addRegularExpressions(Collection<? extends RegularExpression> c) {
		regularExpressions.addAll(c);
		return this;
	}
	
	public int getCountTransitions() {
		return transitions.size();
	}
		
	@Override
	public String toString() {
		return "State" + id;
	}

}
