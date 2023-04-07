package org.iguana.regex;

import org.iguana.regex.visitor.RegularExpressionVisitor;

import java.util.Collections;
import java.util.Set;

import static org.iguana.utils.collections.CollectionsUtil.immutableSet;

public class NewLine extends AbstractRegularExpression {

    private static final Set<CharRange> firstSet = immutableSet(CharRange.in('\r', '\r'), CharRange.in('\n', '\n'));

    private static NewLine instance;

    private NewLine() {
        super(new RegexBuilder<>() {
            @Override
            public NewLine build() {
                return null;
            }
        });
    }

    public static NewLine getInstance() {
        if (instance == null) {
            instance = new NewLine();
        }
        return instance;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Set<CharRange> getFirstSet() {
        return firstSet;
    }

    @Override
    public Set<CharRange> getNotFollowSet() {
        return Collections.emptySet();
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public <T> T accept(RegularExpressionVisitor<T> visitor) {
        return null;
    }

    @Override
    public RegexBuilder<? extends RegularExpression> copy() {
        return null;
    }

    @Override
    public String toString() {
        return "NL";
    }
}
