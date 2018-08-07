package org.iguana.traversal.exception;

import org.iguana.grammar.symbol.Nonterminal;

import java.util.List;

public class CyclicGrammarException extends RuntimeException {

    private final List<Nonterminal> cycle;

    public CyclicGrammarException(List<Nonterminal> cycle) {
        this.cycle = cycle;
    }

    public List<Nonterminal> getCycle() {
        return cycle;
    }

    @Override
    public String toString() {
        return "The SPPF is cyclic, no parse tree can be produced.";
    }
}
