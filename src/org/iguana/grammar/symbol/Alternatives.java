package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Alternatives implements Serializable {

    private static final long serialVersionUID = 1L;

    List<Alternative> alternatives = new ArrayList<>();

    public Alternatives() { }

    public Alternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }
}
