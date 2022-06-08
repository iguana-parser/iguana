package org.iguana.regex;

import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonBuilder;
import org.iguana.regex.automaton.State;
import org.iguana.regex.matcher.DFAMatcher;
import org.iguana.utils.input.Input;

import java.util.Map;

public class IguanaTokenizer {

    private final DFAMatcher matcher;
    private final Map<RegularExpression, String> regularExpressions;
    private final Input input;

    private int inputIndex;
    private Token nextToken;

    public IguanaTokenizer(Map<RegularExpression, String> regularExpressions,
                           Input input,
                           int inputIndex) {
        this.regularExpressions = regularExpressions;
        this.input = input;
        this.inputIndex = inputIndex;

        State startState = new State();
        State finalState = new State();
        for (RegularExpression regularExpression : regularExpressions.keySet()) {
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
            if (inputIndex == input.length() - 1) {
                nextToken = new Token(EOF.getInstance(), EOF.getInstance().toString(), input, inputIndex, inputIndex);
                return false;
            }
            throw new RuntimeException();
        } else if (length != -1) {
            RegularExpression regularExpression = matcher.getMatchedRegularExpression();
            String name = regularExpressions.get(regularExpression);
            nextToken = new Token(regularExpression, name, input, inputIndex, inputIndex + length);
            inputIndex = inputIndex + length;
            return true;
        } else {
            return false;
        }
    }

    public Token nextToken() {
        if (nextToken != null) {
            Token token = nextToken;
            nextToken = null;
            return token;
        }
        return null;
    }
}
