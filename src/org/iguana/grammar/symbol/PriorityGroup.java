package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A priority group is a list of alternatives.
 */
public class PriorityGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Alternative> alternatives = new ArrayList<>();

    public PriorityGroup() { }

    public PriorityGroup(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PriorityGroup)) return false;
        PriorityGroup other = (PriorityGroup) obj;
        return this.alternatives.equals(other.alternatives);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Alternative alternative : alternatives) {
            sb.append(alternative);
            sb.append("\n");
            sb.append("  | ");
        }
        sb.delete(sb.length() - 4, sb.length());
        return sb.toString();
    }
}
