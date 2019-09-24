package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Alternatives represent priority groups
public class Alternatives implements Serializable {

    private static final long serialVersionUID = 1L;

    List<Alternative> alternatives = new ArrayList<>();

    public Alternatives() { }

    public Alternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Alternatives)) return false;
        Alternatives other = (Alternatives) obj;
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
