package org.iguana.grammar;

import iguana.regex.matcher.DFAMatcherFactory;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.grammar.symbol.Terminal;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class GrammarGraph {

    public static final TerminalGrammarSlot epsilonSlot = new TerminalGrammarSlot(Terminal.epsilon(), new DFAMatcherFactory());
    private final List<GrammarSlot> slots;
    private NonterminalGrammarSlot startSlot;

    public GrammarGraph(List<GrammarSlot> slots, NonterminalGrammarSlot startSlot) {
        this.slots = slots;
        this.startSlot = startSlot;
    }

    public List<NonterminalGrammarSlot> getNonterminalGrammarSlots() {
       return slots.stream().filter(slot -> slot instanceof NonterminalGrammarSlot).map(slot -> (NonterminalGrammarSlot) slot).collect(toList());
    }

    public List<TerminalGrammarSlot> getTerminalGrammarSlots() {
        return slots.stream().filter(slot -> slot instanceof TerminalGrammarSlot).map(slot -> (TerminalGrammarSlot) slot).collect(toList());
    }

    public List<BodyGrammarSlot> getBodyGrammarSlots() {
        return slots.stream().filter(slot -> slot instanceof BodyGrammarSlot).map(slot -> (BodyGrammarSlot) slot).collect(toList());
    }

    public NonterminalGrammarSlot getStartSlot() {
        return startSlot;
    }

    public void clear() {
        for (GrammarSlot slot : slots) {
            slot.reset();
        }
    }
}
