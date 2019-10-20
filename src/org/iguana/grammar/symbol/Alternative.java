package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Alternative implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Sequence> seqs;
    
    public final Associativity associativity;

    public Alternative(Builder builder) {
        this.seqs = builder.seqs;
        this.associativity = builder.associativity;
    }

    public Alternative(List<Sequence> sequences, Associativity associativity) {
        this.seqs = sequences;
        this.associativity = associativity;
    }

    public Sequence first() {
        if (seqs == null || seqs.isEmpty()) return null;
        return seqs.get(0);
    }

    public List<Sequence> rest() {
        if (seqs == null || seqs.size() < 2) return null;
        return seqs.subList(1, seqs.size());
    }

    public List<Sequence> seqs() {
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
        for (Sequence seq : seqs) {
            sb.append(seq.toString()).append(" ");
        }
        if (!seqs.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static class Builder {
        private List<Sequence> seqs = new ArrayList<>();
        public Associativity associativity = Associativity.UNDEFINED;

        public Builder addSequence(Sequence seq) {
            seqs.add(seq);
            return this;
        }

        public Builder setAssociativity(Associativity associativity) {
            this.associativity = associativity;
            return this;
        }

        public Alternative build() {
            return new Alternative(this);
        }
    }
}
