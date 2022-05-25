package org.iguana.grammar.transformation;

import org.iguana.regex.Reference;
import org.iguana.regex.RegularExpression;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.SymbolToSymbolVisitor;

import java.util.*;
import java.util.stream.Collectors;

public class ResolveIdentifiers implements GrammarTransformation, SymbolToSymbolVisitor {

    private Set<String> nonterminals;
    private Map<String, RegularExpression> terminals;

    @Override
    public RuntimeGrammar transform(RuntimeGrammar grammar) {
        nonterminals = new HashSet<>();
        for (RuntimeRule rule : grammar.getRules()) {
            nonterminals.add(rule.getHead().getName());
        }
        terminals = grammar.getTerminals();


        List<RuntimeRule> newRules = new ArrayList<>();
        for (RuntimeRule rule : grammar.getRules()) {
            List<Symbol> newSymbols = rule.getBody().stream().map(symbol -> symbol.accept(this)).collect(Collectors.toList());
            RuntimeRule newRule = rule.copyBuilder().setSymbols(newSymbols).build();
            if (newRule.equals(rule)) {
                newRules.add(rule);
            } else {
                newRules.add(newRule);
            }
        }

        Symbol layout = null;
        if (grammar.getLayout() != null) {
            layout = grammar.getLayout().accept(this);
            if (layout instanceof Terminal) {
                layout = ((Terminal) layout).copy().setNodeType(TerminalNodeType.Layout).build();
            } else if (layout instanceof Nonterminal) {
                layout = ((Nonterminal) layout).copy().setNodeType(NonterminalNodeType.Layout).build();
            } else {
                throw new RuntimeException("Layout can only be an instance of a terminal or nonterminal, but was " + layout.getClass().getSimpleName());
            }
        }
        return RuntimeGrammar.builder()
            .addRules(newRules)
            .setLayout(layout)
            .setGlobals(grammar.getGlobals())
            .setEbnfLefts(grammar.getEBNFLefts())
            .setEbnfRights(grammar.getEBNFRights())
            .setStartSymbol(grammar.getStartSymbol()).build();
    }

    @Override
    public Symbol visit(Identifier id) {
        if (nonterminals.contains(id.getName())) {
            return new Nonterminal.Builder(id.getName())
                .addPreConditions(visitPreConditions(id))
                .addPostConditions(visitPostConditions(id))
                .addExcepts(id.getExcepts())
                .setLabel(id.getLabel())
                .build();
        } else if (terminals.containsKey(id.getName())) {
            RegularExpression regularExpression = terminals.get(id.getName());
            return new Terminal.Builder(regularExpression)
                .setNodeType(TerminalNodeType.Regex)
                .setPreConditions(visitPreConditions(id))
                .setPostConditions(visitPostConditions(id))
                .setLabel(id.getLabel())
                .setName(id.getName())
                .build();
        } else {
            throw new RuntimeException("Identifier '" + id + "' is not defined.");
        }
    }

    @Override
    public RegularExpression visit(Reference ref) {
        if (terminals.containsKey(ref.getName())) {
            return terminals.get(ref.getName());
        } else {
            throw new RuntimeException("Terminal '" + ref.getName() + "' is not defined.");
        }
    }
}
