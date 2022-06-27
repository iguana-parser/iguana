// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen;

import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.parsetree.*;

import java.util.List;

public class IggyParseTree {
    // Regexs = Regex+
    public static class Regexs extends NonterminalNode {
        public Regexs(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitRegexs(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Symbol extends NonterminalNode {
        public Symbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Symbol = Identifier Arguments
    public static class CallSymbol extends Symbol {
        public CallSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier child0() {
           return (Identifier) childAt(0);
        }

        public Arguments child1() {
           return (Arguments) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCallSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = 'offside' Symbol
    public static class OffsideSymbol extends Symbol {
        public OffsideSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Symbol child1() {
           return (Symbol) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitOffsideSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Symbol '*'
    public static class StarSymbol extends Symbol {
        public StarSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol child0() {
           return (Symbol) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStarSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Symbol '+'
    public static class PlusSymbol extends Symbol {
        public PlusSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol child0() {
           return (Symbol) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPlusSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Symbol '?'
    public static class OptionSymbol extends Symbol {
        public OptionSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol child0() {
           return (Symbol) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitOptionSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = '(' Symbol Symbol* ')'
    public static class SequenceSymbol extends Symbol {
        public SequenceSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Symbol child1() {
           return (Symbol) childAt(1);
        }

        public MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        public TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitSequenceSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = '(' Symbol+ (| Symbol+)+ ')'
    public static class AlternationSymbol extends Symbol {
        public AlternationSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        public MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        public TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAlternationSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = 'align' Symbol
    public static class AlignSymbol extends Symbol {
        public AlignSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Symbol child1() {
           return (Symbol) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAlignSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = 'ignore' Symbol
    public static class IgnoreSymbol extends Symbol {
        public IgnoreSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Symbol child1() {
           return (Symbol) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitIgnoreSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Identifier ':' Symbol
    public static class LabeledSymbol extends Symbol {
        public LabeledSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier child0() {
           return (Identifier) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Symbol child2() {
           return (Symbol) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitLabeledSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Symbol Statement+
    public static class StatementSymbol extends Symbol {
        public StatementSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol child0() {
           return (Symbol) childAt(0);
        }

        public MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStatementSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Symbol Condition
    public static class PostConditionSymbol extends Symbol {
        public PostConditionSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol child0() {
           return (Symbol) childAt(0);
        }

        public Condition child1() {
           return (Condition) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPostConditionSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Regex '<<' Symbol
    public static class PrecedeSymbol extends Symbol {
        public PrecedeSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex child0() {
           return (Regex) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Symbol child2() {
           return (Symbol) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPrecedeSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Regex '!<<' Symbol
    public static class NotPrecedeSymbol extends Symbol {
        public NotPrecedeSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex child0() {
           return (Regex) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Symbol child2() {
           return (Symbol) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitNotPrecedeSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Symbol '>>' Regex
    public static class FollowSymbol extends Symbol {
        public FollowSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol child0() {
           return (Symbol) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Regex child2() {
           return (Regex) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitFollowSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Symbol '!>>' Regex
    public static class NotFollowSymbol extends Symbol {
        public NotFollowSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol child0() {
           return (Symbol) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Regex child2() {
           return (Regex) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitNotFollowSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Symbol '\' Regex
    public static class ExcludeSymbol extends Symbol {
        public ExcludeSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol child0() {
           return (Symbol) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Regex child2() {
           return (Regex) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitExcludeSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Symbol '!' Identifier
    public static class ExceptSymbol extends Symbol {
        public ExceptSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol child0() {
           return (Symbol) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Identifier child2() {
           return (Identifier) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitExceptSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = 'if' Expression Symbol 'else' Symbol
    public static class IfThenElseSymbol extends Symbol {
        public IfThenElseSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Expression child1() {
           return (Expression) childAt(1);
        }

        public Symbol child2() {
           return (Symbol) childAt(2);
        }

        public TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        public Symbol child4() {
           return (Symbol) childAt(4);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitIfThenElseSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = Identifier
    public static class IdentifierSymbol extends Symbol {
        public IdentifierSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier child0() {
           return (Identifier) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitIdentifierSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = String
    public static class StringSymbol extends Symbol {
        public StringSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStringSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = '{' Symbol Symbol+ '}' '*'
    public static class StarSepSymbol extends Symbol {
        public StarSepSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Symbol child1() {
           return (Symbol) childAt(1);
        }

        public MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        public TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        public TerminalNode child4() {
           return (TerminalNode) childAt(4);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStarSepSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = '{' Symbol Symbol+ '}' '+'
    public static class PlusSepSymbol extends Symbol {
        public PlusSepSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Symbol child1() {
           return (Symbol) childAt(1);
        }

        public MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        public TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        public TerminalNode child4() {
           return (TerminalNode) childAt(4);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPlusSepSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Label = '%' Identifier
    public static class Label extends NonterminalNode {
        public Label(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Identifier child1() {
           return (Identifier) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitLabel(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Global = 'global' Identifier '=' Expression {env = put(env,id.yield)}
    public static class Global extends NonterminalNode {
        public Global(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Identifier child1() {
           return (Identifier) childAt(1);
        }

        public Identifier id() {
           return (Identifier) childAt(1);
        }

        public TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitGlobal(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Name = Identifier
    public static class Name extends NonterminalNode {
        public Name(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier child0() {
           return (Identifier) childAt(0);
        }

        public Identifier id() {
           return (Identifier) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitName(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // RegexBody = RegexSequence*
    public static class RegexBody extends NonterminalNode {
        public RegexBody(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitRegexBody(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // ReturnExpression = '{' Expression '}'
    public static class ReturnExpression extends NonterminalNode {
        public ReturnExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Expression child1() {
           return (Expression) childAt(1);
        }

        public TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitReturnExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Identifier = LetterOrDigits
    public static class Identifier extends NonterminalNode {
        public Identifier(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitIdentifier(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Regex extends NonterminalNode {
        public Regex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Regex = Regex '*'
    public static class StarRegex extends Regex {
        public StarRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex child0() {
           return (Regex) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStarRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = Regex '+'
    public static class PlusRegex extends Regex {
        public PlusRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex child0() {
           return (Regex) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPlusRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = Regex '?'
    public static class OptionRegex extends Regex {
        public OptionRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex child0() {
           return (Regex) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitOptionRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = '(' Regex ')'
    public static class BracketRegex extends Regex {
        public BracketRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Regex child1() {
           return (Regex) childAt(1);
        }

        public TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitBracketRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = '(' Regex Regex+ ')'
    public static class SequenceRegex extends Regex {
        public SequenceRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Regex child1() {
           return (Regex) childAt(1);
        }

        public MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        public TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitSequenceRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = '(' Regexs (| Regexs)+ ')'
    public static class AlternationRegex extends Regex {
        public AlternationRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Regexs child1() {
           return (Regexs) childAt(1);
        }

        public MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        public TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAlternationRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = Name
    public static class NontRegex extends Regex {
        public NontRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Name child0() {
           return (Name) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitNontRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = CharClass
    public static class CharClassRegex extends Regex {
        public CharClassRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public CharClass child0() {
           return (CharClass) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCharClassRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = String
    public static class StringRegex extends Regex {
        public StringRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStringRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // PriorityLevels = Alternative+
    public static class PriorityLevels extends NonterminalNode {
        public PriorityLevels(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPriorityLevels(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Body = PriorityLevels+
    public static class Body extends NonterminalNode {
        public Body(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitBody(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Condition = '[' Expression* ']'
    public static class Condition extends NonterminalNode {
        public Condition(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        public TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCondition(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Binding extends NonterminalNode {
        public Binding(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Binding = VarName '=' Expression
    public static class AssignBinding extends Binding {
        public AssignBinding(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public VarName child0() {
           return (VarName) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAssignBinding(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Binding = 'var' (id:Name {env = put(env,id.yield)} = Expression)+
    public static class DeclareBinding extends Binding {
        public DeclareBinding(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitDeclareBinding(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // RegexSequence = Regex+
    public static class RegexSequence extends NonterminalNode {
        public RegexSequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitRegexSequence(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Parameters = '(' id:Identifier {env = put(env,id.yield)}* ')'
    public static class Parameters extends NonterminalNode {
        public Parameters(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        public TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitParameters(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Alternative extends NonterminalNode {
        public Alternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Alternative = Sequence
    public static class SequenceAlternative extends Alternative {
        public SequenceAlternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Sequence child0() {
           return (Sequence) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitSequenceAlternative(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Alternative = Associativity '(' Sequence (| Sequence)+ ')'
    public static class AssociativityAlternative extends Alternative {
        public AssociativityAlternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Sequence child2() {
           return (Sequence) childAt(2);
        }

        public MetaSymbolNode child3() {
           return (MetaSymbolNode) childAt(3);
        }

        public TerminalNode child4() {
           return (TerminalNode) childAt(4);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAssociativityAlternative(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Alternative = Label?
    public static class EmptyAlternative extends Alternative {
        public EmptyAlternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitEmptyAlternative(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Statement extends NonterminalNode {
        public Statement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Statement = FunName Arguments ;?
    public static class CallStatement extends Statement {
        public CallStatement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Arguments child1() {
           return (Arguments) childAt(1);
        }

        public MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCallStatement(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Statement = Binding ;?
    public static class BindingStatement extends Statement {
        public BindingStatement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Binding child0() {
           return (Binding) childAt(0);
        }

        public MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitBindingStatement(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Definition = (Rule | Global)+
    public static class Definition extends NonterminalNode {
        public Definition(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitDefinition(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Arguments = '(' Expression* ')'
    public static class Arguments extends NonterminalNode {
        public Arguments(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        public TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitArguments(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class CharClass extends NonterminalNode {
        public CharClass(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // CharClass = '[' Range* ']'
    public static class CharsCharClass extends CharClass {
        public CharsCharClass(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        public TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCharsCharClass(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // CharClass = '!' '[' Range* ']'
    public static class NotCharsCharClass extends CharClass {
        public NotCharsCharClass(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        public TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitNotCharsCharClass(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Sequence extends NonterminalNode {
        public Sequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Sequence = Associativity? Condition? Symbol Symbol+ ReturnExpression? Label?
    public static class MoreThanOneElemSequence extends Sequence {
        public MoreThanOneElemSequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        public MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        public Symbol child2() {
           return (Symbol) childAt(2);
        }

        public MetaSymbolNode child3() {
           return (MetaSymbolNode) childAt(3);
        }

        public MetaSymbolNode child4() {
           return (MetaSymbolNode) childAt(4);
        }

        public MetaSymbolNode child5() {
           return (MetaSymbolNode) childAt(5);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitMoreThanOneElemSequence(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Sequence = Condition? Symbol ReturnExpression? Label?
    public static class SingleElemSequence extends Sequence {
        public SingleElemSequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        public Symbol child1() {
           return (Symbol) childAt(1);
        }

        public MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        public MetaSymbolNode child3() {
           return (MetaSymbolNode) childAt(3);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitSingleElemSequence(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Range extends NonterminalNode {
        public Range(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Range = RangeChar '-' RangeChar
    public static class RangeRange extends Range {
        public RangeRange(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitRangeRange(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Range = RangeChar
    public static class CharacterRange extends Range {
        public CharacterRange(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCharacterRange(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Expression extends NonterminalNode {
        public Expression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Expression = FunName Arguments
    public static class CallExpression extends Expression {
        public CallExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Arguments child1() {
           return (Arguments) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCallExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = '!' Expression
    public static class NotExpression extends Expression {
        public NotExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Expression child1() {
           return (Expression) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitNotExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '*' Expression
    public static class MultiplicationExpression extends Expression {
        public MultiplicationExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitMultiplicationExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '/' Expression
    public static class DivisionExpression extends Expression {
        public DivisionExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitDivisionExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '+' Expression
    public static class AdditionExpression extends Expression {
        public AdditionExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAdditionExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '-' Expression
    public static class SubtractionExpression extends Expression {
        public SubtractionExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitSubtractionExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '>=' Expression
    public static class GreaterEqExpression extends Expression {
        public GreaterEqExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitGreaterEqExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '<=' Expression
    public static class LessEqExpression extends Expression {
        public LessEqExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitLessEqExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '>' Expression
    public static class GreaterExpression extends Expression {
        public GreaterExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitGreaterExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '<' Expression
    public static class LessExpression extends Expression {
        public LessExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitLessExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '==' Expression
    public static class EqualExpression extends Expression {
        public EqualExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitEqualExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '!=' Expression
    public static class NotEqualExpression extends Expression {
        public NotEqualExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitNotEqualExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '&&' Expression
    public static class AndExpression extends Expression {
        public AndExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAndExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Expression '||' Expression
    public static class OrExpression extends Expression {
        public OrExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child0() {
           return (Expression) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Expression child2() {
           return (Expression) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitOrExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Identifier '.l'
    public static class LExtentExpression extends Expression {
        public LExtentExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier child0() {
           return (Identifier) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitLExtentExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Identifier '.r'
    public static class RExtentExpression extends Expression {
        public RExtentExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier child0() {
           return (Identifier) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitRExtentExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Identifier '.yield'
    public static class YieldExpression extends Expression {
        public YieldExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier child0() {
           return (Identifier) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitYieldExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Identifier '.val'
    public static class ValExpression extends Expression {
        public ValExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier child0() {
           return (Identifier) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitValExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = VarName
    public static class NameExpression extends Expression {
        public NameExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public VarName child0() {
           return (VarName) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitNameExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = Number
    public static class NumberExpression extends Expression {
        public NumberExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitNumberExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = '(' Expression ')'
    public static class BracketExpression extends Expression {
        public BracketExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        public Expression child1() {
           return (Expression) childAt(1);
        }

        public TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitBracketExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Rule extends NonterminalNode {
        public Rule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Rule = (start | layout)? Name Parameters? '=' Body
    public static class ContextFreeRule extends Rule {
        public ContextFreeRule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        public Name child1() {
           return (Name) childAt(1);
        }

        public MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        public TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        public Body child4() {
           return (Body) childAt(4);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitContextFreeRule(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Rule = layout? 'regex' Name '=' RegexBody
    public static class RegexRule extends Rule {
        public RegexRule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        public TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        public Name child2() {
           return (Name) childAt(2);
        }

        public TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        public RegexBody child4() {
           return (RegexBody) childAt(4);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitRegexRule(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // VarName = Identifier
    public static class VarName extends NonterminalNode {
        public VarName(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier child0() {
           return (Identifier) childAt(0);
        }

        public Identifier id() {
           return (Identifier) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitVarName(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

}
