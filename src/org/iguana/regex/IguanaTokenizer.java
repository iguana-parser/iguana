package org.iguana.regex;

import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonBuilder;
import org.iguana.regex.automaton.State;
import org.iguana.regex.matcher.DFAMatcher;
import org.iguana.utils.input.Input;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class IguanaTokenizer {

    private final DFAMatcher matcher;
    private final Map<RegularExpression, String> regularExpressionCategories;

    private Input input;
    private int inputIndex;

    public IguanaTokenizer(Collection<RegularExpression> regularExpressions) {
        this(Collections.emptyMap(), createMatcher(regularExpressions));
    }

    public IguanaTokenizer(Map<RegularExpression, String> regularExpressionCategories, DFAMatcher matcher) {
        this.regularExpressionCategories = regularExpressionCategories;
        this.matcher = matcher;
    }

    public static DFAMatcher createMatcher(Collection<RegularExpression> regularExpressions) {
        int order = 0;
        State startState = new State();
        State finalState = new State();
        for (RegularExpression regularExpression : regularExpressions) {
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
        return new DFAMatcher(automaton);
    }

    public void prepare(Input input, int inputIndex) {
        this.input = input;
        this.inputIndex = inputIndex;
    }

    public Token nextToken() {
        return nextToken(i -> false);
    }

    /**
     * - Returns the next token, corresponding to the longest matching regular expression, from the
     *   current input index.
     * - Returns an error token with length 1 when the next character cannot be matched.
     */
    public Token nextToken(FollowTest followTest) {
        if (followTest.test(input.charAt(inputIndex))) {
            return null;
        }
        if (inputIndex == input.length() - 1) {
            return new Token(EOF.getInstance(), EOF.getInstance().toString(), input, inputIndex, inputIndex);
        }
        int length = matcher.match(input, inputIndex);
        if (length >= 0) {
            RegularExpression regularExpression = matcher.getMatchedRegularExpression();
            String category = regularExpressionCategories.get(regularExpression);
            Token nextToken = new Token(regularExpression, category, input, inputIndex, inputIndex + length);
            inputIndex = inputIndex + length;
            return nextToken;
        } else {
            Token nextToken = new Token(Error.getInstance(), Error.getInstance().toString(), input, inputIndex, inputIndex + 1);
            inputIndex++;
            return nextToken;
        }
    }
}
