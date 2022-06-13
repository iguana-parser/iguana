package org.iguana.grammar.transformation;

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

    public ResolveIdentifiers(Set<String> nonterminals, Map<String, RegularExpression> regularExpressions) {
        this.nonterminals = nonterminals;
        this.regularExpressions = regularExpressions;
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
        } else if (regularExpressions.containsKey(id.getName())) {
            RegularExpression regularExpression = regularExpressions.get(id.getName());
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
        if (regularExpressions.containsKey(ref.getName())) {
            return regularExpressions.get(ref.getName());
        } else {
            throw new RuntimeException("Terminal '" + ref.getName() + "' is not defined.");
        }
    }
}
