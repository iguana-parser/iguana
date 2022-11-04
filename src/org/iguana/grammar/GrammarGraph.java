package org.iguana.grammar;

import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.matcher.DFAMatcherFactory;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class GrammarGraph {

    public static final TerminalGrammarSlot epsilonSlot =
        new TerminalGrammarSlot(Terminal.epsilon(), new DFAMatcherFactory());
    private final List<GrammarSlot> slots;
    private final Map<String, Expression> globals;
    private final Map<Nonterminal, NonterminalGrammarSlot> nonterminalsMap;

    public GrammarGraph(
        List<GrammarSlot> slots,
        Map<Nonterminal, NonterminalGrammarSlot> nonterminalsMap,
        Map<String, Expression> globals
    ) {
        this.slots = slots;
        this.nonterminalsMap = nonterminalsMap;
        this.globals = globals;
    }

    public List<NonterminalGrammarSlot> getNonterminalGrammarSlots() {
       return slots.stream().filter(slot -> slot instanceof NonterminalGrammarSlot)
                            .map(slot -> (NonterminalGrammarSlot) slot).collect(toList());
    }

    public List<TerminalGrammarSlot> getTerminalGrammarSlots() {
        return slots.stream().filter(slot -> slot instanceof TerminalGrammarSlot)
                             .map(slot -> (TerminalGrammarSlot) slot).collect(toList());
    }

    public List<BodyGrammarSlot> getBodyGrammarSlots() {
        return slots.stream().filter(slot -> slot instanceof BodyGrammarSlot)
                             .map(slot -> (BodyGrammarSlot) slot).collect(toList());
    }

    public NonterminalGrammarSlot getStartSlot(Nonterminal nonterminal) {
        return nonterminalsMap.get(nonterminal);
    }

    public void clear() {
        for (GrammarSlot slot : slots) {
            slot.reset();
        }
    }

    public Map<String, Expression> getGlobals() {
        return globals;
    }
}
