package org.iguana.parsetree;

public interface ParseTreeBuilder {
    Object visit(TerminalNode node);
    Object visit(RuleNode node);
    Object visit(AmbiguityNode node);
    Object visit(StarNode node);
    Object visit(PlusNode node);
    Object visit(AltNode node);
    Object visit(SequenceNode node);
    Object visit(OptionNode node);
}
