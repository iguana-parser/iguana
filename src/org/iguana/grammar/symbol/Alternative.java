package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.List;

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
}
