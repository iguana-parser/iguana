package org.iguana.regex;

import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonBuilder;
import org.iguana.regex.automaton.State;
import org.iguana.regex.matcher.DFAMatcher;
import org.iguana.utils.input.Input;

import java.util.Map;

public class IguanaTokenizer {

    private final DFAMatcher matcher;
    private final Map<RegularExpression, String> regularExpressionCategories;

    private Input input;
    private int inputIndex;
    private Token nextToken;

    public IguanaTokenizer(Map<RegularExpression, String> regularExpressionCategories) {
        this.regularExpressionCategories = regularExpressionCategories;

        int order = 0;
        State startState = new State();
        State finalState = new State();
        for (RegularExpression regularExpression : regularExpressionCategories.keySet()) {
            Automaton automaton = regularExpression.getAutomaton();
            for (State state : automaton.getFinalStates()) {
                state.addRegularExpression(regularExpression, order++);
            }
            startState.addEpsilonTransition(automaton.getStartState());
            for (State automatonFinalState : automaton.getFinalStates()) {
                automatonFinalState.addEpsilonTransition(finalState);
            }
        }
        Automaton automaton = new AutomatonBuilder(startState).build();
        this.matcher = new DFAMatcher(automaton);
    }

    public void prepare(Input input, int inputIndex) {
        this.input = input;
        this.inputIndex = inputIndex;
    }

    public boolean hasNextToken() {
        if (input == null) {
            throw new IllegalStateException("The prepare method should be called first.");
        }
        if (nextToken != null) return true;
        if (inputIndex >= input.length() - 1) return false;
        int length = matcher.match(input, inputIndex);
        if (length == 0) {
            if (inputIndex == input.length() - 1) {
                nextToken = new Token(EOF.getInstance(), EOF.getInstance().toString(), input, inputIndex, inputIndex);
                return false;
            }
            throw new RuntimeException();
        } else if (length != -1) {
            RegularExpression regularExpression = matcher.getMatchedRegularExpression();
            String category = regularExpressionCategories.get(regularExpression);
            nextToken = new Token(regularExpression, category, input, inputIndex, inputIndex + length);
            inputIndex = inputIndex + length;
            return true;
        } else {
            nextToken = new Token(null, "Error", input, inputIndex, inputIndex + 1);
            inputIndex++;
            return true;
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
