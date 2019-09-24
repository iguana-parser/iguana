package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Alternative implements Serializable {

    private static final long serialVersionUID = 1L;

    public Seq first;
    public List<Seq> rest;
    public Associativity associativity;

    public Alternative() { }

    public Alternative(Seq sequence) {
        this.first = sequence;
        this.rest = null;
        this.associativity = null;
    }
    
    public Alternative(Seq sequence, List<Seq> sequences, Associativity associativity) {
        this.first = sequence;
        this.rest = sequences;
        this.associativity = associativity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Alternative)) return false;
        Alternative other = (Alternative) obj;
        return Objects.equals(this.first, other.first) &&
                Objects.equals(this.rest, other.rest) &&
                Objects.equals(this.associativity, other.associativity);
    }

    @Override
    public String toString() {
        if (first == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (rest == null) {
            sb.append(first.toString());
        } else {
            sb.append(associativity).append(": ");
            sb.append(first.toString()).append(" ");
            for (Seq seq : rest) {
                sb.append(seq.toString()).append(" ");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
