package org.iguana.parsetree;


import org.iguana.grammar.symbol.*;

import java.util.ArrayList;
import java.util.List;

public interface ParseTreeVisitor {

    Object visitNonterminalNode(NonterminalNode node);

    default Object visitAmbiguityNode(AmbiguityNode node) {
        throw new RuntimeException("Ambiguity");
    }

    default Object visitTerminalNode(TerminalNode node) {
        return null;
    }

    default Object visitMetaSymbolNode(MetaSymbolNode node) {
        Symbol symbol = node.getGrammarDefinition();

        boolean shouldBeFlattened = (symbol instanceof Star || symbol instanceof Plus || symbol instanceof Opt) && getSymbol(symbol) instanceof Group;

        // Flatten sequence inside star and plus
        if (shouldBeFlattened) {
            if (symbol instanceof Opt) {
                if (node.children().size() == 0) {
                    return null;
                }
                List<Object> result = (List<Object>) node.childAt(0).accept(this);
                if (result.size() == 1) {
                    return result.get(0);
                }
                return result;
            } else {
                int size = node.children().size();
                List<Object> result = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    ParseTreeNode child = node.childAt(i);
                    result.addAll((List<Object>) child.accept(this));
                }
                return result;
            }
        }

        if (symbol instanceof Star || symbol instanceof Plus || symbol instanceof Group) {
            List<Object> result = new ArrayList<>(node.children().size());
            for (int i = 0; i < node.children().size(); i++) {
                ParseTreeNode child = node.childAt(i);
                Object childResult = child.accept(this);
                if (childResult != null) {
                    result.add(childResult);
                }
            }
            return result;
        } else if (symbol instanceof Alt) {
            return node.childAt(0).accept(this);
        } else { // Opt
            if (node.children().size() == 0) {
                return null;
            }
            return node.childAt(0).accept(this);
        }
    }

    default Object visitChildren(ParseTreeNode node) {
        int size = node.children().size();

        if (size == 1) {
            return node.childAt(0).accept(this);
        }

        List<Object> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            ParseTreeNode child = node.childAt(i);
            Object childResult = child.accept(this);
            if (childResult != null) {
                result.add(childResult);
            }
        }

        if (result.isEmpty()) {
            return null;
        }

        if (result.size() == 1) {
            return result.get(0);
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
