package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Alternative implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Seq> seqs;
    public Associativity associativity;

    public Alternative() { }

    public Alternative(Seq sequence) {
        this(sequence, null, null);
    }

    public Alternative(Seq sequence, List<Seq> sequences, Associativity associativity) {
        seqs = new ArrayList<>();
        if (sequence != null) seqs.add(sequence);
        if (sequences != null) seqs.addAll(sequences);
        this.associativity = associativity;
    }

    public Seq first() {
        if (seqs == null || seqs.isEmpty()) return null;
        return seqs.get(0);
    }

    public List<Seq> rest() {
        if (seqs == null || seqs.size() < 2) return null;
        return seqs.subList(1, seqs.size());
    }

    public List<Seq> seqs() {
        return seqs == null ? Collections.emptyList() : seqs;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Alternative)) return false;
        Alternative other = (Alternative) obj;
        return Objects.equals(this.seqs(), other.seqs()) && Objects.equals(this.associativity, other.associativity);
    }

    @Override
    public String toString() {
        if (seqs == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (associativity != null) {
            sb.append(associativity).append(": ");
        }
        for (Seq seq : seqs) {
            sb.append(seq.toString()).append(" ");
        }
        if (!seqs.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
