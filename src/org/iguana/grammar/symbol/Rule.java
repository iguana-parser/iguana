package org.iguana.grammar.symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A rule is comprised of a list of priority levels, which are separated by &gt; in the textual grammar notation.
 * In the following example, there are two priority groups, one consisting of the alternative A B | C D,
 * and second of the alternative E F | G H.
 *
 * S : A B
 *   | C D
 *   &gt; E F
 *   | G H
 *   ;
 */
public class Rule {

    private final Nonterminal head;

    private final List<PriorityLevel> priorityLevels;

    private final LayoutStrategy layoutStrategy;

    public Rule(Builder builder) {
        this.head = builder.head;
        this.priorityLevels = builder.priorityLevels;
        this.layoutStrategy = builder.layoutStrategy;
    }

    public Nonterminal getHead() {
        return head;
    }

    public List<PriorityLevel> getPriorityLevels() {
        return priorityLevels;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Rule)) return false;
        Rule other = (Rule) obj;
        return this.head.equals(other.head) && this.priorityLevels.equals(other.priorityLevels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, priorityLevels);
    }

    public LayoutStrategy getLayoutStrategy() {
        return layoutStrategy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (head.toString().length() == 1) {
            sb.append(head).append(" =");
        } else {
            sb.append(head).append("\n  =");
        }
        if (priorityLevels.isEmpty()) {
            sb.append("\n");
        } else {
            for (PriorityLevel priorityLevel : priorityLevels) {
                sb.append(priorityLevel).append("  > ");
            }
        }
        sb.delete(sb.length() - 4, sb.length());
        sb.append("\n");
        return sb.toString();
    }

    public static class Builder {
        private Nonterminal head;
        private final List<PriorityLevel> priorityLevels = new ArrayList<>();
        private LayoutStrategy layoutStrategy = LayoutStrategy.INHERITED;

        private Builder() { }

        public Builder(Nonterminal head) {
            this.head = head;
        }

        public Builder addPriorityLevel(PriorityLevel priorityLevel) {
            priorityLevels.add(priorityLevel);
            return this;
        }

        public Builder addPriorityLevels(List<PriorityLevel> priorityLevels) {
            this.priorityLevels.addAll(priorityLevels);
            return this;
        }

        public Builder setLayoutStrategy(LayoutStrategy layoutStrategy) {
            this.layoutStrategy = layoutStrategy;
            return this;
        }

        public Rule build() {
            return new Rule(this);
        }
    }

}
