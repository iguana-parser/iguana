package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A production rule is a group of priority groups, which are separated by > in the textual grammar notation.
 * For example, in the following example, there are two priority groups, one consisting of the alternative A B | C D,
 * and second of the alternative E F | G H.
 *
 * S : A B
 *   | C D
 *   > E F
 *   | G H
 *   ;
 */
public class HighLevelRule implements Serializable {

    private static final long serialVersionUID = 1L;

    final Nonterminal head;

    final List<PriorityGroup> priorityGroups;

    final List<String> parameters;

    public HighLevelRule(Builder builder) {
        this.head = builder.head;
        this.parameters = builder.parameters;
        this.priorityGroups = builder.alternativesList;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HighLevelRule)) return false;
        HighLevelRule other = (HighLevelRule) obj;
        return this.head.equals(other.head) && this.parameters.equals(other.parameters) && this.priorityGroups.equals(other.priorityGroups);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(head).append("\n  : ");
        if (priorityGroups.isEmpty()) {
            sb.append("\n");
        }
        for (PriorityGroup alternatives : priorityGroups) {
            sb.append(alternatives).append("  > ");
        }
        sb.delete(sb.length() - 4, sb.length());
        sb.append("  ;");
        return sb.toString();
    }

    public static class Builder {
        List<PriorityGroup> alternativesList;
        Nonterminal head;
        List<String> parameters;

        public Builder() {}

        public Builder(Nonterminal head, List<String> parameters) {
            this.head = head;
            this.parameters = parameters;
        }

        public Builder(Nonterminal head, List<String> parameters, List<PriorityGroup> alternativesList) {
            this.head = head;
            this.parameters = parameters;
            this.alternativesList = alternativesList;
        }

        public Builder addAlternative(PriorityGroup alternatives) {
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
