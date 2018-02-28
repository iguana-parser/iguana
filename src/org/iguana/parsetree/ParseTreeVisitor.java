package org.iguana.parsetree;


public interface ParseTreeVisitor<T> {
    T visit(NonterminalNode node);
    T visit(AmbiguityNode node);
    T visit(TerminalNode node);
    T visit(MetaSymbolNode node);
}
