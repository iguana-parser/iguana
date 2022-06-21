package org.iguana.parsetree;


import org.iguana.grammar.symbol.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface ParseTreeVisitor<T> {

    T visitNonterminalNode(NonterminalNode node);

    default List<T> visitAmbiguityNode(AmbiguityNode node) {
        throw new RuntimeException("Ambiguity");
    }

    default T visitTerminalNode(TerminalNode node) {
        return null;
    }

    default List<T> visitMetaSymbolNode(MetaSymbolNode node) {
        Symbol symbol = node.getGrammarDefinition();

        boolean shouldBeFlattened = (symbol instanceof Star || symbol instanceof Plus || symbol instanceof Opt) && getSymbol(symbol) instanceof Group;

        // Flatten sequence inside star and plus
        if (shouldBeFlattened) {
            if (symbol instanceof Opt) {
                if (node.children().size() == 0) {
                    return null;
                }
                return (List<T>) node.childAt(0).accept(this);
            } else {
                int size = node.children().size();
                List<T> result = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    ParseTreeNode child = node.childAt(i);
                    List<T> childResult = (List<T>) child.accept(this);
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
        List<T> result = new ArrayList<>(node.children().size());
        for (int i = 0; i < node.children().size(); i++) {
            ParseTreeNode child = node.childAt(i);
            T childResult = (T) child.accept(this);
            if (childResult != null) {
                result.add(childResult);
            }
        }

        return result;
    }

    default List<T> visitChildren(ParseTreeNode node) {
        int size = node.children().size();

        List<T> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            ParseTreeNode child = node.childAt(i);
            T childResult = (T) child.accept(this);
            if (childResult != null) {
                if (childResult instanceof List<?>) {
                    result.addAll((List<T>) childResult);
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
