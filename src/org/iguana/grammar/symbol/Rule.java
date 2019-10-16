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
public class Rule implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Nonterminal head;

    private final List<String> parameters;

    private final List<PriorityLevel> priorityLevels;

    public Rule(Builder builder) {
        this.head = builder.head;
        this.parameters = builder.parameters;
        this.priorityLevels = builder.priorityLevels;
    }

    public Nonterminal getHead() {
        return head;
    }

    public List<PriorityLevel> getPriorityLevels() {
        return priorityLevels;
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Rule)) return false;
        Rule other = (Rule) obj;
        return this.head.equals(other.head) && this.parameters.equals(other.parameters) && this.priorityLevels.equals(other.priorityLevels);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(head).append("\n  : ");
        if (priorityLevels.isEmpty()) {
            sb.append("\n");
        }
        for (PriorityLevel alternatives : priorityLevels) {
            sb.append(alternatives).append("  > ");
        }
        sb.delete(sb.length() - 4, sb.length());
        sb.append("  ;");
        return sb.toString();
    }

    public static class Builder {
        List<PriorityLevel> priorityLevels;
        Nonterminal head;
        List<String> parameters;

        public Builder() {}

        public Builder(Nonterminal head, List<String> parameters) {
            this.head = head;
            this.parameters = parameters;
        }

        public Builder(Nonterminal head, List<String> parameters, List<PriorityLevel> priorityLevels) {
            this.head = head;
            this.parameters = parameters;
            this.priorityLevels = priorityLevels;
        }

        public Builder addPriorityGroup(PriorityLevel priorityLevel) {
            if (priorityLevels == null) {
                priorityLevels = new ArrayList<>();
            }
            this.priorityLevels.add(priorityLevel);
            return this;
        }

        public Rule build() {
            if (priorityLevels == null) priorityLevels = Collections.emptyList();
            if (parameters == null) parameters = Collections.emptyList();
            return new Rule(this);
        }
    }
}
