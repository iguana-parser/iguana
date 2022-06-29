package org.iguana.parsetree;


import org.iguana.grammar.symbol.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ParseTreeVisitor<T> {

    T visitNonterminalNode(NonterminalNode node);

    default List<T> visitAmbiguityNode(AmbiguityNode node) {
        throw new RuntimeException("Ambiguity");
    }

    default T visitTerminalNode(TerminalNode node) {
        return null;
    }

    default List<T> visitStarNode(MetaSymbolNode.StarNode node) {
        return visitStarOrPlusNode(node);
    }

    default List<T> visitPlusNode(MetaSymbolNode.PlusNode node) {
        return visitStarOrPlusNode(node);
    }

    default Optional<T> visitOptionNode(MetaSymbolNode.OptionNode node) {
        if (node.children().size() == 0) {
            return Optional.empty();
        }
        return Optional.of((T) node.childAt(0).accept(this));
    }

    default T visitStartNode(MetaSymbolNode.StartNode node) {
        return (T) node.childAt(0).accept(this);
    }

    default T visitAltNode(MetaSymbolNode.AltNode node) {
        return (T) node.childAt(0).accept(this);
    }

    default List<T> visitGroupNode(MetaSymbolNode.GroupNode node) {
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

    default List<T> visitStarOrPlusNode(MetaSymbolNode node) {
        if (node.children().size() == 0) {
            return Collections.emptyList();
        }

        Symbol symbol = node.getGrammarDefinition();

        if (getSymbol(symbol) instanceof Group) {
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

    default List<? extends T> visitChildren(ParseTreeNode node) {
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

    default boolean shouldBeFlatted(Symbol symbol) {
        return (symbol instanceof Star || symbol instanceof Plus || symbol instanceof Opt) && getSymbol(symbol) instanceof Group;
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
