// This file has been generated, do not directly edit this file!
package org.iguana.iggy.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.parsetree.*;

import java.util.List;


public class IggyParseTree {
    public static class Regexs extends NonterminalNode {
        public Regexs(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Symbol extends NonterminalNode {
        public Symbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Identifier Arguments
    public static class CallSymbol extends Symbol {
        public CallSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Identifier child0() {
           return (Identifier) childAt(0);
        }

        Arguments child1() {
           return (Arguments) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = 'offside' Symbol
    public static class OffsideSymbol extends Symbol {
        public OffsideSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Symbol child1() {
           return (Symbol) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Symbol '*'
    public static class StarSymbol extends Symbol {
        public StarSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Symbol child0() {
           return (Symbol) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Symbol '+'
    public static class PlusSymbol extends Symbol {
        public PlusSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Symbol child0() {
           return (Symbol) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Symbol '?'
    public static class OptionSymbol extends Symbol {
        public OptionSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Symbol child0() {
           return (Symbol) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = '(' Symbol Symbol* ')'
    public static class SequenceSymbol extends Symbol {
        public SequenceSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Symbol child1() {
           return (Symbol) childAt(1);
        }

        MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = '(' Symbol+ (| Symbol+)+ ')'
    public static class AlternationSymbol extends Symbol {
        public AlternationSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = 'align' Symbol
    public static class AlignSymbol extends Symbol {
        public AlignSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Symbol child1() {
           return (Symbol) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = 'ignore' Symbol
    public static class IgnoreSymbol extends Symbol {
        public IgnoreSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Symbol child1() {
           return (Symbol) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Identifier ':' Symbol
    public static class LabeledSymbol extends Symbol {
        public LabeledSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Identifier child0() {
           return (Identifier) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Symbol child2() {
           return (Symbol) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Symbol Statement+
    public static class StatementSymbol extends Symbol {
        public StatementSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Symbol child0() {
           return (Symbol) childAt(0);
        }

        MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Symbol Condition
    public static class PostConditionSymbol extends Symbol {
        public PostConditionSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Symbol child0() {
           return (Symbol) childAt(0);
        }

        Condition child1() {
           return (Condition) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Regex '<<' Symbol
    public static class PrecedeSymbol extends Symbol {
        public PrecedeSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Regex child0() {
           return (Regex) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Symbol child2() {
           return (Symbol) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Regex '!<<' Symbol
    public static class NotPrecedeSymbol extends Symbol {
        public NotPrecedeSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Regex child0() {
           return (Regex) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Symbol child2() {
           return (Symbol) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Symbol '>>' Regex
    public static class FollowSymbol extends Symbol {
        public FollowSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Symbol child0() {
           return (Symbol) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Regex child2() {
           return (Regex) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Symbol '!>>' Regex
    public static class NotFollowSymbol extends Symbol {
        public NotFollowSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Symbol child0() {
           return (Symbol) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Regex child2() {
           return (Regex) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Symbol '\' Regex
    public static class ExcludeSymbol extends Symbol {
        public ExcludeSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Symbol child0() {
           return (Symbol) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Regex child2() {
           return (Regex) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Symbol '!' Identifier
    public static class ExceptSymbol extends Symbol {
        public ExceptSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Symbol child0() {
           return (Symbol) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Identifier child2() {
           return (Identifier) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = 'if' Expression Symbol 'else' Symbol
    public static class IfThenElseSymbol extends Symbol {
        public IfThenElseSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Expression child1() {
           return (Expression) childAt(1);
        }

        Symbol child2() {
           return (Symbol) childAt(2);
        }

        TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        Symbol child4() {
           return (Symbol) childAt(4);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = Identifier
    public static class IdentifierSymbol extends Symbol {
        public IdentifierSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Identifier child0() {
           return (Identifier) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = String
    public static class StringSymbol extends Symbol {
        public StringSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = '{' Symbol Symbol+ '}' '*'
    public static class StarSepSymbol extends Symbol {
        public StarSepSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Symbol child1() {
           return (Symbol) childAt(1);
        }

        MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        TerminalNode child4() {
           return (TerminalNode) childAt(4);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Symbol = '{' Symbol Symbol+ '}' '+'
    public static class PlusSepSymbol extends Symbol {
        public PlusSepSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Symbol child1() {
           return (Symbol) childAt(1);
        }

        MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        TerminalNode child4() {
           return (TerminalNode) childAt(4);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Label extends NonterminalNode {
        public Label(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Identifier child1() {
           return (Identifier) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Global extends NonterminalNode {
        public Global(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Identifier child1() {
           return (Identifier) childAt(1);
        }

        Identifier id() {
           return (Identifier) childAt(1);
        }

        TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Name extends NonterminalNode {
        public Name(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Identifier child0() {
           return (Identifier) childAt(0);
        }

        Identifier id() {
           return (Identifier) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class RegexBody extends NonterminalNode {
        public RegexBody(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class ReturnExpression extends NonterminalNode {
        public ReturnExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Expression child1() {
           return (Expression) childAt(1);
        }

        TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Identifier extends NonterminalNode {
        public Identifier(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Regex extends NonterminalNode {
        public Regex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Regex = Regex '*'
    public static class StarRegex extends Regex {
        public StarRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Regex child0() {
           return (Regex) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Regex = Regex '+'
    public static class PlusRegex extends Regex {
        public PlusRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Regex child0() {
           return (Regex) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Regex = Regex '?'
    public static class OptionRegex extends Regex {
        public OptionRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Regex child0() {
           return (Regex) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Regex = '(' Regex ')'
    public static class BracketRegex extends Regex {
        public BracketRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Regex child1() {
           return (Regex) childAt(1);
        }

        TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Regex = '(' Regex Regex+ ')'
    public static class SequenceRegex extends Regex {
        public SequenceRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Regex child1() {
           return (Regex) childAt(1);
        }

        MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Regex = '(' Regexs (| Regexs)+ ')'
    public static class AlternationRegex extends Regex {
        public AlternationRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Regexs child1() {
           return (Regexs) childAt(1);
        }

        MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Regex = Name
    public static class NontRegex extends Regex {
        public NontRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Name child0() {
           return (Name) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Regex = CharClass
    public static class CharClassRegex extends Regex {
        public CharClassRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        CharClass child0() {
           return (CharClass) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Regex = String
    public static class StringRegex extends Regex {
        public StringRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class PriorityLevels extends NonterminalNode {
        public PriorityLevels(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Body extends NonterminalNode {
        public Body(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Condition extends NonterminalNode {
        public Condition(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Binding extends NonterminalNode {
        public Binding(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Binding = VarName '=' Expression
    public static class AssignBinding extends Binding {
        public AssignBinding(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        VarName child0() {
           return (VarName) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Binding = 'var' (id:Name {env = put(env,id.yield)} = Expression)+
    public static class DeclareBinding extends Binding {
        public DeclareBinding(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class RegexSequence extends NonterminalNode {
        public RegexSequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Parameters extends NonterminalNode {
        public Parameters(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Alternative extends NonterminalNode {
        public Alternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Alternative = Sequence
    public static class SequenceAlternative extends Alternative {
        public SequenceAlternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Sequence child0() {
           return (Sequence) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Alternative = Associativity '(' Sequence (| Sequence)+ ')'
    public static class AssociativityAlternative extends Alternative {
        public AssociativityAlternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Sequence child2() {
           return (Sequence) childAt(2);
        }

        MetaSymbolNode child3() {
           return (MetaSymbolNode) childAt(3);
        }

        TerminalNode child4() {
           return (TerminalNode) childAt(4);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Alternative = Label?
    public static class EmptyAlternative extends Alternative {
        public EmptyAlternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Statement extends NonterminalNode {
        public Statement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Statement = FunName Arguments ;?
    public static class CallStatement extends Statement {
        public CallStatement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Arguments child1() {
           return (Arguments) childAt(1);
        }

        MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Statement = Binding ;?
    public static class BindingStatement extends Statement {
        public BindingStatement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Binding child0() {
           return (Binding) childAt(0);
        }

        MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Definition extends NonterminalNode {
        public Definition(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Arguments extends NonterminalNode {
        public Arguments(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class CharClass extends NonterminalNode {
        public CharClass(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // CharClass = '[' Range* ']'
    public static class CharsCharClass extends CharClass {
        public CharsCharClass(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // CharClass = '!' '[' Range* ']'
    public static class NotCharsCharClass extends CharClass {
        public NotCharsCharClass(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Sequence extends NonterminalNode {
        public Sequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Sequence = Associativity? Condition? Symbol Symbol+ ReturnExpression? Label?
    public static class MoreThanOneElemSequence extends Sequence {
        public MoreThanOneElemSequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        MetaSymbolNode child1() {
           return (MetaSymbolNode) childAt(1);
        }

        Symbol child2() {
           return (Symbol) childAt(2);
        }

        MetaSymbolNode child3() {
           return (MetaSymbolNode) childAt(3);
        }

        MetaSymbolNode child4() {
           return (MetaSymbolNode) childAt(4);
        }

        MetaSymbolNode child5() {
           return (MetaSymbolNode) childAt(5);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Sequence = Condition? Symbol ReturnExpression? Label?
    public static class SingleElemSequence extends Sequence {
        public SingleElemSequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        Symbol child1() {
           return (Symbol) childAt(1);
        }

        MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        MetaSymbolNode child3() {
           return (MetaSymbolNode) childAt(3);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Range extends NonterminalNode {
        public Range(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Range = RangeChar '-' RangeChar
    public static class RangeRange extends Range {
        public RangeRange(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Range = RangeChar
    public static class CharacterRange extends Range {
        public CharacterRange(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Expression extends NonterminalNode {
        public Expression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = FunName Arguments
    public static class CallExpression extends Expression {
        public CallExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Arguments child1() {
           return (Arguments) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = '!' Expression
    public static class NotExpression extends Expression {
        public NotExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Expression child1() {
           return (Expression) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '*' Expression
    public static class MultiplicationExpression extends Expression {
        public MultiplicationExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '/' Expression
    public static class DivisionExpression extends Expression {
        public DivisionExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '+' Expression
    public static class AdditionExpression extends Expression {
        public AdditionExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '-' Expression
    public static class SubtractionExpression extends Expression {
        public SubtractionExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '>=' Expression
    public static class GreaterEqExpression extends Expression {
        public GreaterEqExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '<=' Expression
    public static class LessEqExpression extends Expression {
        public LessEqExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '>' Expression
    public static class GreaterExpression extends Expression {
        public GreaterExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '<' Expression
    public static class LessExpression extends Expression {
        public LessExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '==' Expression
    public static class EqualExpression extends Expression {
        public EqualExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '!=' Expression
    public static class NotEqualExpression extends Expression {
        public NotEqualExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '&&' Expression
    public static class AndExpression extends Expression {
        public AndExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Expression '||' Expression
    public static class OrExpression extends Expression {
        public OrExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Expression child0() {
           return (Expression) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Expression child2() {
           return (Expression) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Identifier '.l'
    public static class LExtentExpression extends Expression {
        public LExtentExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Identifier child0() {
           return (Identifier) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Identifier '.r'
    public static class RExtentExpression extends Expression {
        public RExtentExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Identifier child0() {
           return (Identifier) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Identifier '.yield'
    public static class YieldExpression extends Expression {
        public YieldExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Identifier child0() {
           return (Identifier) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Identifier '.val'
    public static class ValExpression extends Expression {
        public ValExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Identifier child0() {
           return (Identifier) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = VarName
    public static class NameExpression extends Expression {
        public NameExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        VarName child0() {
           return (VarName) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = Number
    public static class NumberExpression extends Expression {
        public NumberExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Expression = '(' Expression ')'
    public static class BracketExpression extends Expression {
        public BracketExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        TerminalNode child0() {
           return (TerminalNode) childAt(0);
        }

        Expression child1() {
           return (Expression) childAt(1);
        }

        TerminalNode child2() {
           return (TerminalNode) childAt(2);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class Rule extends NonterminalNode {
        public Rule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Rule = (start | layout)? Name Parameters? '=' Body
    public static class ContextFreeRule extends Rule {
        public ContextFreeRule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        Name child1() {
           return (Name) childAt(1);
        }

        MetaSymbolNode child2() {
           return (MetaSymbolNode) childAt(2);
        }

        TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        Body child4() {
           return (Body) childAt(4);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    // Rule = layout? 'regex' Name '=' RegexBody
    public static class RegexRule extends Rule {
        public RegexRule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        MetaSymbolNode child0() {
           return (MetaSymbolNode) childAt(0);
        }

        TerminalNode child1() {
           return (TerminalNode) childAt(1);
        }

        Name child2() {
           return (Name) childAt(2);
        }

        TerminalNode child3() {
           return (TerminalNode) childAt(3);
        }

        RegexBody child4() {
           return (RegexBody) childAt(4);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

    public static class VarName extends NonterminalNode {
        public VarName(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        Identifier child0() {
           return (Identifier) childAt(0);
        }

        Identifier id() {
           return (Identifier) childAt(0);
        }

        // @Override
        public void accept(IggyParseTreeVisitor visitor) {
        }
    }

}
