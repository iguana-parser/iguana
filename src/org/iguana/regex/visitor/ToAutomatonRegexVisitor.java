package org.iguana.regex.visitor;

import org.iguana.regex.EOF;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonBuilder;
import org.iguana.regex.automaton.State;

import java.util.*;

public class ToAutomatonRegexVisitor implements RegularExpressionVisitor<Automaton> {

	private Map<RegularExpression, Automaton> cache = new HashMap<>();

	@Override
	public Automaton visit(org.iguana.regex.Char c) {
		Automaton automaton = cache.get(c);
		if (automaton == null) {
			org.iguana.regex.automaton.State startState = new org.iguana.regex.automaton.State();
			org.iguana.regex.automaton.State finalState = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
			finalState.addRegularExpression(c);
			startState.addTransition(new org.iguana.regex.automaton.Transition(c.getValue(), finalState));
			automaton = Automaton.builder(startState).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(org.iguana.regex.CharRange r) {
		Automaton automaton = cache.get(r);
		if (automaton == null) {
			org.iguana.regex.automaton.State startState = new org.iguana.regex.automaton.State();
			org.iguana.regex.automaton.State finalState = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
			finalState.addRegularExpression(r);
			startState.addTransition(new org.iguana.regex.automaton.Transition(r.getStart(), r.getEnd(), finalState));
			automaton = Automaton.builder(startState).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(org.iguana.regex.EOF eof) {
		Automaton automaton = cache.get(eof);
		if (automaton == null) {
			org.iguana.regex.automaton.State startState = new org.iguana.regex.automaton.State();
			org.iguana.regex.automaton.State endState = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
			endState.addRegularExpression(eof);
			startState.addTransition(new org.iguana.regex.automaton.Transition(EOF.VALUE, endState));
			automaton = Automaton.builder(startState).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(org.iguana.regex.Epsilon e) {
		Automaton automaton = cache.get(e);
		if (automaton == null) {
			org.iguana.regex.automaton.State state = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
			state.addRegularExpression(e);
			return Automaton.builder(state).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(org.iguana.regex.Star star) {
		//TODO: add separators to the DFA
		Automaton automaton = cache.get(star);
		if (automaton == null) {
			org.iguana.regex.automaton.State startState = new org.iguana.regex.automaton.State();
			org.iguana.regex.automaton.State finalState = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
			finalState.addRegularExpression(star);

			Automaton starAutomaton = star.getSymbol().accept(this).copy();

			startState.addEpsilonTransition(starAutomaton.getStartState());

			Set<org.iguana.regex.automaton.State> finalStates = starAutomaton.getFinalStates();

			for(org.iguana.regex.automaton.State s : finalStates) {
				s.setStateType(org.iguana.regex.automaton.StateType.NORMAL);
				s.addEpsilonTransition(finalState);
				s.addEpsilonTransition(starAutomaton.getStartState());
			}

			startState.addEpsilonTransition(finalState);

			automaton = Automaton.builder(startState).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(org.iguana.regex.Plus p) {
		Automaton automaton = cache.get(p);
		if (automaton == null) {
			automaton = org.iguana.regex.Seq.from(p.getSymbol(), org.iguana.regex.Star.from(p.getSymbol())).accept(this);
		}
		return automaton;
	}

	@Override
	public Automaton visit(org.iguana.regex.Opt opt) {
		Automaton automaton = cache.get(opt);
		if (automaton == null) {
			automaton = opt.getSymbol().accept(this).copy();

			Set<org.iguana.regex.automaton.State> finalStates = automaton.getFinalStates();
			for(org.iguana.regex.automaton.State finalState : finalStates) {
				automaton.getStartState().addEpsilonTransition(finalState);
				finalState.addRegularExpression(opt);
			}
		}
		return automaton;
	}

	@Override
	public <E extends RegularExpression> Automaton visit(org.iguana.regex.Alt<E> alt) {
		Automaton automaton = cache.get(alt);
		if (automaton == null) {
			List<E> symbols = alt.getSymbols();

			if (symbols.size() == 1)
				return symbols.get(0).accept(this);

			List<Automaton> automatons = new ArrayList<>();

			for (RegularExpression e : symbols) {
				automatons.add(e.accept(this).copy());
			}

			org.iguana.regex.automaton.State startState = new org.iguana.regex.automaton.State();

			for (Automaton a : automatons) {
				startState.addEpsilonTransition(a.getStartState());
				for (org.iguana.regex.automaton.State finalState : a.getFinalStates()) {
					finalState.addRegularExpression(alt);
				}
			}

			automaton = new AutomatonBuilder(startState).build();
		}
		return automaton;
	}

	@Override
	public <E extends RegularExpression> Automaton visit(org.iguana.regex.Seq<E> seq) {
		Automaton automaton = cache.get(seq);
		if (automaton == null) {
			List<Automaton> automatons = new ArrayList<>();

			for (E symbol : seq.getSymbols()) {
				automatons.add(symbol.accept(this).copy());
			}

			Automaton current = automatons.get(0);
			org.iguana.regex.automaton.State startState = current.getStartState();

			for (int i = 1; i < automatons.size(); i++) {
				Automaton next = automatons.get(i);

				for (org.iguana.regex.automaton.State s : current.getFinalStates()) {
					s.setStateType(org.iguana.regex.automaton.StateType.NORMAL);
					// Merge the end state with the start state of the next automaton
					for (org.iguana.regex.automaton.Transition t : next.getStartState().getTransitions()) {
						s.addTransition(new org.iguana.regex.automaton.Transition(t.getRange(), t.getDestination()));
					}
				}

				current = next;
			}

			for (State finalState : current.getFinalStates()) {
				finalState.addRegularExpression(seq);
			}

			automaton = Automaton.builder(startState).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(org.iguana.regex.Reference ref) {
		throw new RuntimeException("References should be resolved first");
	}

}
