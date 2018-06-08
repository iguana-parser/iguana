package iguana.regex;

import iguana.regex.automaton.*;

import java.util.*;

public class ToAutomatonRegexVisitor implements RegularExpressionVisitor<Automaton> {

	private Map<RegularExpression, Automaton> cache = new HashMap<>();

	@Override
	public Automaton visit(Char c) {
		Automaton automaton = cache.get(c);
		if (automaton == null) {
			State startState = new State();
			State finalState = new State(StateType.FINAL);
			finalState.addRegularExpression(c);
			startState.addTransition(new Transition(c.getValue(), finalState));
			automaton = Automaton.builder(startState).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(CharRange r) {
		Automaton automaton = cache.get(r);
		if (automaton == null) {
			State startState = new State();
			State finalState = new State(StateType.FINAL);
			finalState.addRegularExpression(r);
			startState.addTransition(new Transition(r.getStart(), r.getEnd(), finalState));
			automaton = Automaton.builder(startState).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(EOF eof) {
		Automaton automaton = cache.get(eof);
		if (automaton == null) {
			State startState = new State();
			State endState = new State(StateType.FINAL);
			endState.addRegularExpression(eof);
			startState.addTransition(new Transition(EOF.VALUE, endState));
			automaton = Automaton.builder(startState).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(Epsilon e) {
		Automaton automaton = cache.get(e);
		if (automaton == null) {
			State state = new State(StateType.FINAL);
			state.addRegularExpression(e);
			return Automaton.builder(state).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(Star star) {
		//TODO: add separators to the DFA
		Automaton automaton = cache.get(star);
		if (automaton == null) {
			State startState = new State();
			State finalState = new State(StateType.FINAL);
			finalState.addRegularExpression(star);

			Automaton starAutomaton = star.getSymbol().accept(this).copy();

			startState.addEpsilonTransition(starAutomaton.getStartState());

			Set<State> finalStates = starAutomaton.getFinalStates();

			for(State s : finalStates) {
				s.setStateType(StateType.NORMAL);
				s.addEpsilonTransition(finalState);
				s.addEpsilonTransition(starAutomaton.getStartState());
			}

			startState.addEpsilonTransition(finalState);

			automaton = Automaton.builder(startState).build();
		}
		return automaton;
	}

	@Override
	public Automaton visit(Plus p) {
		Automaton automaton = cache.get(p);
		if (automaton == null) {
			automaton = Seq.from(p.getSymbol(), Star.from(p.getSymbol())).accept(this);
		}
		return automaton;
	}

	@Override
	public Automaton visit(Opt opt) {
		Automaton automaton = cache.get(opt);
		if (automaton == null) {
			automaton = opt.getSymbol().accept(this).copy();

			Set<State> finalStates = automaton.getFinalStates();
			for(State finalState : finalStates) {
				automaton.getStartState().addEpsilonTransition(finalState);
				finalState.addRegularExpression(opt);
			}
		}
		return automaton;
	}

	@Override
	public <E extends RegularExpression> Automaton visit(Alt<E> alt) {
		Automaton automaton = cache.get(alt);
		if (automaton == null) {
			List<E> symbols = alt.getSymbols();

			if (symbols.size() == 1)
				return symbols.get(0).accept(this);

			List<Automaton> automatons = new ArrayList<>();

			for (RegularExpression e : symbols) {
				automatons.add(e.accept(this).copy());
			}

			State startState = new State();

			for (Automaton a : automatons) {
				startState.addEpsilonTransition(a.getStartState());
				for (State finalState : a.getFinalStates()) {
					finalState.addRegularExpression(alt);
				}
			}

			automaton = new AutomatonBuilder(startState).build();
		}
		return automaton;
	}

	@Override
	public <E extends RegularExpression> Automaton visit(Seq<E> seq) {
		Automaton automaton = cache.get(seq);
		if (automaton == null) {
			List<Automaton> automatons = new ArrayList<>();

			for (E symbol : seq.getSymbols()) {
				automatons.add(symbol.accept(this).copy());
			}

			Automaton current = automatons.get(0);
			State startState = current.getStartState();

			for (int i = 1; i < automatons.size(); i++) {
				Automaton next = automatons.get(i);

				for (State s : current.getFinalStates()) {
					s.setStateType(StateType.NORMAL);
					// Merge the end state with the start state of the next automaton
					for (Transition t : next.getStartState().getTransitions()) {
						s.addTransition(new Transition(t.getRange(), t.getDestination()));
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

}
