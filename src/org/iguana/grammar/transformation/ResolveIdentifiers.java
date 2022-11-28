package org.iguana.grammar.transformation;

import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.Identifier;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.Reference;
import org.iguana.regex.RegularExpression;
import org.iguana.traversal.SymbolToSymbolVisitor;

import java.util.Map;
import java.util.Set;

public class ResolveIdentifiers implements SymbolToSymbolVisitor {

    private final Set<String> nonterminals;
    private final Map<String, RegularExpression> regularExpressions;
    private final Set<String> layouts;

    public ResolveIdentifiers(
        Set<String> nonterminals,
        Map<String, RegularExpression> regularExpressions,
        Set<String> layouts
    ) {
        this.nonterminals = nonterminals;
        this.regularExpressions = regularExpressions;
        this.layouts = layouts;
    }

    @Override
    public Symbol visit(Identifier id) {
        if (nonterminals.contains(id.getName())) {
            NonterminalNodeType nodeType;
            if (layouts.contains(id.getName())) {
                nodeType = NonterminalNodeType.Layout;
            } else {
                nodeType = NonterminalNodeType.Basic;
            }
            return new Nonterminal.Builder(id.getName())
                .addPreConditions(visitPreConditions(id))
                .addPostConditions(visitPostConditions(id))
                .addExcepts(id.getExcepts())
                .setLabel(id.getLabel())
                .setNodeType(nodeType)
                .build();
        } else if (regularExpressions.containsKey(id.getName())) {
            RegularExpression regularExpression = regularExpressions.get(id.getName());
            TerminalNodeType nodeType;
            if (layouts.contains(id.getName())) {
                nodeType = TerminalNodeType.Layout;
            } else {
                nodeType = TerminalNodeType.Regex;
            }
            return new Terminal.Builder(regularExpression)
                .setNodeType(nodeType)
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
        if (regularExpressions.containsKey(ref.getName())) {
            return regularExpressions.get(ref.getName());
        } else {
            throw new RuntimeException("Terminal '" + ref.getName() + "' is not defined.");
        }
    }
}
