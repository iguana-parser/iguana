package org.iguana.regex;

import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.visitor.ToAutomatonRegexVisitor;

import java.util.Set;

public abstract class AbstractRegularExpression implements RegularExpression {

    private final Set<CharRange> lookaheads;

    private final Set<CharRange> lookbehinds;

    private Automaton automaton;

    public AbstractRegularExpression(RegexBuilder<? extends  RegularExpression> builder ) {
        this.lookaheads = builder.lookaheads;
        this.lookbehinds = builder.lookbehinds;
    }

    @Override
    public Set<CharRange> getLookaheads() {
        return lookaheads;
    }

    @Override
    public Set<CharRange> getLookbehinds() {
        return lookbehinds;
    }

    public Automaton getAutomaton() {
        if (automaton == null) {
            automaton = accept(new ToAutomatonRegexVisitor());
        }
        return automaton;
    }

}
