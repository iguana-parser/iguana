package org.iguana.regex.visitor;

import org.iguana.regex.*;
import org.iguana.regex.automaton.*;

import java.util.*;

public class ToAutomatonRegexVisitor implements RegularExpressionVisitor<Automaton> {

    // TODO: consider making this cache global, across the regular expressions for the whole grammar.
    private final Map<RegularExpression, Automaton> cache = new HashMap<>();

    @Override
    public Automaton visit(Char c) {
        return cache.computeIfAbsent(c, k -> {
            State startState = new State();
            State finalState = new State(StateType.FINAL);
            finalState.addRegularExpression(c);
            startState.addTransition(new Transition(c.getValue(), finalState));
            return Automaton.builder(startState).build();
        });
    }

    @Override
    public Automaton visit(CharRange r) {
        return cache.computeIfAbsent(r, k -> {
            State startState = new State();
            State finalState = new State(StateType.FINAL);
            finalState.addRegularExpression(r);
            startState.addTransition(new Transition(r.getStart(), r.getEnd(), finalState));
            return Automaton.builder(startState).build();
        });
    }

    @Override
    public Automaton visit(EOF eof) {
        return cache.computeIfAbsent(eof, k -> {
            State startState = new State();
            State finalState = new State(StateType.FINAL);
            finalState.addRegularExpression(eof);
            startState.addTransition(new Transition(EOF.VALUE, finalState));
            return Automaton.builder(startState).build();
        });
    }

    @Override
    public Automaton visit(Epsilon e) {
        return cache.computeIfAbsent(e, k -> {
            State state = new State(StateType.FINAL);
            state.addRegularExpression(e);
            return Automaton.builder(state).build();
        });
    }

    @Override
    public Automaton visit(Star star) {
        //TODO: add separators to the DFA
        return cache.computeIfAbsent(star, k -> {
            State startState = new State();
            State finalState = new State(StateType.FINAL);
            finalState.addRegularExpression(star);

            Automaton starAutomaton = star.getSymbol().accept(this).copy();

            startState.addEpsilonTransition(starAutomaton.getStartState());

            Set<State> finalStates = starAutomaton.getFinalStates();

            for (State s : finalStates) {
                s.setStateType(StateType.NORMAL);
                s.addEpsilonTransition(finalState);
                s.addEpsilonTransition(starAutomaton.getStartState());
            }

            startState.addEpsilonTransition(finalState);

            return Automaton.builder(startState).build();
        });
    }

    @Override
    public Automaton visit(Plus p) {
        return cache.computeIfAbsent(p, k -> Seq.from(p.getSymbol(), Star.from(p.getSymbol())).accept(this));
    }

    @Override
    public Automaton visit(Opt opt) {
        return cache.computeIfAbsent(opt, k -> {
            Automaton automaton = opt.getSymbol().accept(this).copy();

            Set<State> finalStates = automaton.getFinalStates();
            for (State finalState : finalStates) {
                automaton.getStartState().addEpsilonTransition(finalState);
                finalState.addRegularExpression(opt);
            }
            return automaton;
        });
    }

    @Override
    public <E extends RegularExpression> Automaton visit(Alt<E> alt) {
        return cache.computeIfAbsent(alt, k -> {
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

            return new AutomatonBuilder(startState).build();
        });
    }

    @Override
    public <E extends RegularExpression> Automaton visit(Seq<E> seq) {
        return cache.computeIfAbsent(seq, k -> {
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

            return Automaton.builder(startState).build();
        });
    }

    @Override
    public Automaton visit(org.iguana.regex.Reference ref) {
        throw new RuntimeException("References should be resolved first");
    }

}
