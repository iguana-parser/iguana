package org.iguana.regex;

import java.util.Set;

public abstract class AbstractRegularExpression implements RegularExpression {

    private final String name;

    private final Object object;

    private final Set<CharacterRange> lookaheads;

    private final Set<CharacterRange> lookbehinds;

    public AbstractRegularExpression(RegexBuilder<? extends  RegularExpression> builder ) {
        this.name = builder.name;
        this.object = builder.object;
        this.lookaheads = builder.lookaheads;
        this.lookbehinds = builder.lookbehinds;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public Set<CharacterRange> getLookaheads() {
        return lookaheads;
    }

    @Override
    public Set<CharacterRange> getLookbehinds() {
        return lookbehinds;
    }

}
