package org.iguana.grammar.symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * A priority group is a list of alternatives.
 */
public class PriorityLevel {

    private final List<Alternative> alternatives;

    public static PriorityLevel from(Alternative ...alternatives) {
        return new PriorityLevel.Builder().addAlternatives(List.of(alternatives)).build();
    }

    public PriorityLevel(Builder builder) {
        this.alternatives = builder.alternatives;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PriorityLevel)) return false;
        PriorityLevel other = (PriorityLevel) obj;
        return this.alternatives.equals(other.alternatives);
    }

    @Override
    public int hashCode() {
        return alternatives.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Alternative alternative : alternatives) {
            sb.append(alternative);
            sb.append("\n  | ");
        }
        sb.delete(sb.length() - 5, sb.length());
        return sb.toString();
    }

    public static class Builder {
        private List<Alternative> alternatives = new ArrayList<>();

        public Builder addAlternative(Alternative alternative) {
            alternatives.add(alternative);
            return this;
        }

        public Builder addAlternatives(List<Alternative> alternatives) {
            this.alternatives.addAll(alternatives);
            return this;
        }

        public PriorityLevel build() {
            if (alternatives.isEmpty()) {
                throw new RuntimeException("Alternatives cannot be empty");
            }
            return new PriorityLevel(this);
        }
    }
}
