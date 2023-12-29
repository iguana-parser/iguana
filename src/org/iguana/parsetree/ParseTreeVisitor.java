package org.iguana.parsetree;


import org.iguana.grammar.symbol.Group;
import org.iguana.grammar.symbol.Opt;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.symbol.Symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ParseTreeVisitor<T> {

    T visitNonterminalNode(NonterminalNode node);

    T visitTerminalNode(TerminalNode node);

    T visitErrorNode(ErrorNode node);

    default List<T> visitAmbiguityNode(AmbiguityNode node) {
        throw new RuntimeException("Ambiguity");
    }

    default List<T> visitStarNode(MetaSymbolNode.StarNode node) {
        return visitStarOrPlusNode(node);
    }

    default List<T> visitPlusNode(MetaSymbolNode.PlusNode node) {
        return visitStarOrPlusNode(node);
    }

    default Optional<T> visitOptionNode(MetaSymbolNode.OptionNode node) {
        if (node.children().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of((T) node.childAt(0).accept(this));
    }

    default List<T> visitStartNode(MetaSymbolNode.StartNode node) {
        return visitList(node.children());
    }

    default T visitAltNode(MetaSymbolNode.AltNode node) {
        return (T) node.childAt(0).accept(this);
    }

    default List<T> visitGroupNode(MetaSymbolNode.GroupNode node) {
        return visitList(node.children());
    }

    default List<T> visitList(List<ParseTreeNode> children) {
        int size = children.size();
        List<T> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ParseTreeNode child = children.get(i);
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

    default <U extends T> List<U> visitChildren(ParseTreeNode node) {
        int size = node.children().size();

        List<U> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            ParseTreeNode child = node.childAt(i);
            U childResult = (U) child.accept(this);
            if (childResult != null) {
                if (childResult instanceof List<?>) {
                    result.addAll((List<U>) childResult);
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
