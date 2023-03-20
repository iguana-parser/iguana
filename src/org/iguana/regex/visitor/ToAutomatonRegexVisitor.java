package org.iguana.regex.visitor;

import org.iguana.regex.Alt;
import org.iguana.regex.Char;
import org.iguana.regex.CharRange;
import org.iguana.regex.EOF;
import org.iguana.regex.Epsilon;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Seq;
import org.iguana.regex.Star;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonBuilder;
import org.iguana.regex.automaton.State;
import org.iguana.regex.automaton.StateType;
import org.iguana.regex.automaton.Transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ToAutomatonRegexVisitor implements RegularExpressionVisitor<Automaton> {

    // TODO: consider making this cache global, across the regular expressions for the whole grammar.
    private final Map<RegularExpression, Automaton> cache = new HashMap<>();

    // Since Java 9, recursive calls to computeIfAbsent throw concurrent modification exception.
    // This memoize function is a way to provide the same concise interface but avoid the exceptions.
    private Automaton memoize(RegularExpression regex, Function<RegularExpression, Automaton> f) {
        Automaton automaton = cache.get(regex);
        if (automaton != null) return automaton;
        automaton = f.apply(regex);
        cache.put(regex, automaton);
        return automaton;
    }

    @Override
    public Automaton visit(Char c) {
        return memoize(c, regex -> {
            State startState = new State();
            State finalState = new State(StateType.FINAL);
            startState.addTransition(new Transition(c.getValue(), finalState));
            return Automaton.builder(startState).build();
        });
    }

    @Override
    public Automaton visit(CharRange r) {
        return memoize(r, regex -> {
            State startState = new State();
            State finalState = new State(StateType.FINAL);
            startState.addTransition(new Transition(r.getStart(), r.getEnd(), finalState));
            return Automaton.builder(startState).build();
        });
    }

    @Override
    public Automaton visit(EOF eof) {
        return memoize(eof, regex -> {
            State startState = new State();
            State finalState = new State(StateType.FINAL);
            startState.addTransition(new Transition(EOF.VALUE, finalState));
            return Automaton.builder(startState).build();
        });
    }

    @Override
    public Automaton visit(Epsilon e) {
        return memoize(e, regex -> {
            State state = new State(StateType.FINAL);
            return Automaton.builder(state).build();
        });
    }

    @Override
    public Automaton visit(Star star) {
        //TODO: add separators to the DFA
        return memoize(star, regex -> {
            State startState = new State();
            State finalState = new State(StateType.FINAL);

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
        return memoize(p, regex -> Seq.from(p.getSymbol(), Star.from(p.getSymbol())).accept(this));
    }

    @Override
    public Automaton visit(Opt opt) {
        return memoize(opt, regex -> {
            Automaton automaton = opt.getSymbol().accept(this).copy();

            Set<State> finalStates = automaton.getFinalStates();
            for (State finalState : finalStates) {
                automaton.getStartState().addEpsilonTransition(finalState);
            }
            return automaton;
        });
    }

    @Override
    public <E extends RegularExpression> Automaton visit(Alt<E> alt) {
        return memoize(alt, regex -> {
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
            }

            return new AutomatonBuilder(startState).build();
        });
    }

    @Override
    public <E extends RegularExpression> Automaton visit(Seq<E> seq) {
        return memoize(seq, regex -> {
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

            return Automaton.builder(startState).build();
        });
    }

    @Override
    public Automaton visit(org.iguana.regex.Reference ref) {
        throw new RuntimeException("References should be resolved first");
    }

}
