package org.iguana.parsetree;


import org.iguana.grammar.symbol.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface ParseTreeVisitor {

    Object visitNonterminalNode(NonterminalNode node);

    default Object visitAmbiguityNode(AmbiguityNode node) {
        throw new RuntimeException("Ambiguity");
    }

    default Object visitTerminalNode(TerminalNode node) {
        return null;
    }

    default List<Object> visitMetaSymbolNode(MetaSymbolNode node) {
        Symbol symbol = node.getGrammarDefinition();

        boolean shouldBeFlattened = (symbol instanceof Star || symbol instanceof Plus || symbol instanceof Opt) && getSymbol(symbol) instanceof Group;

        // Flatten sequence inside star and plus
        if (shouldBeFlattened) {
            if (symbol instanceof Opt) {
                if (node.children().size() == 0) {
                    return null;
                }
                return (List<Object>) node.childAt(0).accept(this);
            } else {
                int size = node.children().size();
                List<Object> result = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    ParseTreeNode child = node.childAt(i);
                    List<Object> childResult = (List<Object>) child.accept(this);
                    // This can happen when we have lists with separators, e.g., {A ','}*
                    if (childResult != null) {
                        result.addAll(childResult);
                    }
                }
                return result;
            }
        }

        if (node.children().size() == 0) {
            return Collections.emptyList();
        }
        List<Object> result = new ArrayList<>(node.children().size());
        for (int i = 0; i < node.children().size(); i++) {
            ParseTreeNode child = node.childAt(i);
            Object childResult = child.accept(this);
            if (childResult != null) {
                result.add(childResult);
            }
        }

        return result;
    }

    default List<Object> visitChildren(ParseTreeNode node) {
        int size = node.children().size();

        List<Object> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            ParseTreeNode child = node.childAt(i);
            Object childResult = child.accept(this);
            if (childResult != null) {
                if (childResult instanceof List<?>) {
                    result.addAll((List<?>) childResult);
                } else {
                    result.add(childResult);
                }
            }
        }

        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        return result;
    }

    static Symbol getSymbol(Symbol symbol) {
        if (symbol instanceof Star) {
            return ((Star) symbol).getSymbol();
        } else if (symbol instanceof Plus) {
            return ((Plus) symbol).getSymbol();
        } else if (symbol instanceof Opt) {
            return ((Opt) symbol).getSymbol();
        } else throw new RuntimeException("Unsupported symbol " + symbol);
    }
}
