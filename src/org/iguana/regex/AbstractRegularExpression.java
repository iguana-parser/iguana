package org.iguana.regex;

import java.util.Set;

public abstract class AbstractRegularExpression implements RegularExpression {

    private static final long serialVersionUID = 1L;

    private final Set<CharRange> lookaheads;

    private final Set<CharRange> lookbehinds;

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

}
