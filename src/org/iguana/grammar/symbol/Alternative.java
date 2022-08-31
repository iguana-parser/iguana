package org.iguana.grammar.symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Alternative {

    private final List<Sequence> seqs;
    
    public final Associativity associativity;

    public Alternative from(List<Sequence> seqs) {
        return new Builder().addSequences(seqs).build();
    }

    public Alternative(Builder builder) {
        this.seqs = builder.seqs;
        this.associativity = builder.associativity;
    }

    public Sequence first() {
        if (seqs.isEmpty()) return null;
        return seqs.get(0);
    }

    public List<Sequence> rest() {
        if (seqs.size() < 2) return null;
        return seqs.subList(1, seqs.size());
    }

    public Associativity getAssociativity() {
        return associativity;
    }

    public List<Sequence> seqs() {
        return seqs;
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
        StringBuilder sb = new StringBuilder();
        if (seqs.size() > 1) {
            if (associativity != Associativity.UNDEFINED) {
                sb.append(associativity).append(" ");
            }
            sb.append("(");
            for (Sequence seq : seqs) {
                sb.append(seq).append("\n  |       ");
            }
            sb.delete(sb.length() - 11, sb.length());
            sb.append(")");

        } else if (seqs.size() == 1) {
            if (associativity != Associativity.UNDEFINED) {
                sb.append(associativity).append(" ").append(seqs.get(0));
            } else {
                sb.append(seqs.get(0));
            }
        }
        return sb.toString();
    }

    public static class Builder {
        private List<Sequence> seqs = new ArrayList<>();
        private Associativity associativity = Associativity.UNDEFINED;

        public Builder() { }

        public Builder(List<Sequence> sequences, Associativity associativity) {
            if (sequences == null) throw new RuntimeException("Sequences cannot be null.");
            if (associativity == null) throw new RuntimeException("Sequences cannot be null.");
            this.seqs = sequences;
            this.associativity = associativity;
        }

        public Builder addSequence(Sequence seq) {
            seqs.add(seq);
            return this;
        }

        public Builder addSequences(List<Sequence> seqs) {
            this.seqs.addAll(seqs);
            return this;
        }

        public Builder setAssociativity(Associativity associativity) {
            this.associativity = associativity;
            return this;
        }

        public Alternative build() {
            if (associativity == null) {
                throw new RuntimeException("Associativity cannot be null.");
            }
            if (seqs.isEmpty()) {
                seqs = Collections.emptyList();
            }
            return new Alternative(this);
        }
    }
}
