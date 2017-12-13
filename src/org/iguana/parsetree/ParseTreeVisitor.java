package org.iguana.parsetree;


public interface ParseTreeVisitor<T> {
    T visit(NonterminalNode node);
    T visit(AmbiguityNode node);
    T visit(ListNode node);
    T visit(TerminalNode node);
    T visit(StarNode node);
    T visit(PlusNode node);
    T visit(AltNode node);
    T visit(SequenceNode node);
    T visit(OptionNode node);
}
