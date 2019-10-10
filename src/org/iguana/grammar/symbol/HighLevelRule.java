package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighLevelRule implements Serializable {

    private static final long serialVersionUID = 1L;

    final Nonterminal head;
    final List<Alternatives> alternativesList;
    final List<String> parameters;

    public HighLevelRule(Builder builder) {
        this.head = builder.head;
        this.parameters = builder.parameters;
        this.alternativesList = builder.alternativesList;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HighLevelRule)) return false;
        HighLevelRule other = (HighLevelRule) obj;
        return this.head.equals(other.head) && this.parameters.equals(other.parameters) && this.alternativesList.equals(other.alternativesList);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(head).append("\n  : ");
        if (alternativesList.isEmpty()) {
            sb.append("\n");
        }
        for (Alternatives alternatives : alternativesList) {
            sb.append(alternatives).append("  > ");
        }
        sb.delete(sb.length() - 4, sb.length());
        sb.append("  ;");
        return sb.toString();
    }

    public static class Builder {
        List<Alternatives> alternativesList;
        Nonterminal head;
        List<String> parameters;

        public Builder() {}

        public Builder(Nonterminal head, List<String> parameters) {
            this.head = head;
            this.parameters = parameters;
        }

        public Builder(Nonterminal head, List<String> parameters, List<Alternatives> alternativesList) {
            this.head = head;
            this.parameters = parameters;
            this.alternativesList = alternativesList;
        }

        public Builder addAlternative(Alternatives alternatives) {
            if (alternativesList == null) {
                alternativesList = new ArrayList<>();
            }
            this.alternativesList.add(alternatives);
            return this;
        }

        public HighLevelRule build() {
            if (alternativesList == null) alternativesList = Collections.emptyList();
            if (parameters == null) parameters = Collections.emptyList();
            return new HighLevelRule(this);
        }
    }
}
