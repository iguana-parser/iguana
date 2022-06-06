package org.iguana.regex;

import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonBuilder;
import org.iguana.regex.automaton.State;
import org.iguana.regex.matcher.DFAMatcher;
import org.iguana.utils.input.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IguanaTokenizer {

    private final DFAMatcher matcher;
    private final Input input;

    private int inputIndex;
    private String nextToken;

    public IguanaTokenizer(Map<String, RegularExpression> regularExpressionsMap,
                           Set<RegularExpression> literals,
                           Input input,
                           int inputIndex) {
        this.input = input;
        this.inputIndex = inputIndex;

        List<RegularExpression> regularExpressions = new ArrayList<>();
        regularExpressions.addAll(regularExpressionsMap.values());
        regularExpressions.addAll(literals);

        State startState = new State();
        State finalState = new State();
        for (RegularExpression regularExpression : regularExpressions) {
            Automaton automaton = regularExpression.getAutomaton();
            startState.addEpsilonTransition(automaton.getStartState());
            for (State automatonFinalState : automaton.getFinalStates()) {
                automatonFinalState.addEpsilonTransition(finalState);
            }
        }
        Automaton automaton = new AutomatonBuilder(startState).build();
        this.matcher = new DFAMatcher(automaton);
    }

    public boolean hasNextToken() {
        if (nextToken != null) return true;
        if (inputIndex >= input.length()) return false;
        int length = matcher.match(input, inputIndex);
        if (length == 0) {
            nextToken = "";
            inputIndex++;
            return true;
        } else if (length != -1) {
            nextToken = input.subString(inputIndex, inputIndex + length);
            inputIndex = inputIndex + length;
            return true;
        } else {
            return false;
        }
    }

    public String nextToken() {
        String value = nextToken;
        nextToken = null;
        return value;
    }
}
