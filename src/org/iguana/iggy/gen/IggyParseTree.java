// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen;

import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.parsetree.*;

import java.util.List;

import static org.iguana.parsetree.MetaSymbolNode.*;

public class IggyParseTree {
    // Grammar = 'grammar'? name:Identifier? defs:(Rule | TopLevelVar)+
    public static class Grammar extends NonterminalNode {
        public Grammar(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public OptionNode name() {
           return (OptionNode) childAt(1);
        }

        public PlusNode defs() {
           return (PlusNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitGrammar(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // TopLevelVar = 'global'? 'var' id:Identifier '=' exp:exp:Expression {env = put(env,id.yield)}
    public static class TopLevelVar extends NonterminalNode {
        public TopLevelVar(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier id() {
           return (Identifier) childAt(2);
        }

        public Expression exp() {
           return (Expression) childAt(4);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitTopLevelVar(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Rule extends NonterminalNode {
        public Rule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Rule = modifier:('start' | 'layout' | 'lexical')? name:Name params:Parameters? '=' body:Body
    public static class ContextFreeRule extends Rule {
        public ContextFreeRule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public OptionNode modifier() {
           return (OptionNode) childAt(0);
        }

        public Name name() {
           return (Name) childAt(1);
        }

        public OptionNode params() {
           return (OptionNode) childAt(2);
        }

        public Body body() {
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

    // Rule = modifier:'layout'? 'regex' name:Name '=' body:RegexBody
    public static class RegexRule extends Rule {
        public RegexRule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public OptionNode modifier() {
           return (OptionNode) childAt(0);
        }

        public Name name() {
           return (Name) childAt(2);
        }

        public RegexBody body() {
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

    // Parameters = '(' {id:Identifier {env = put(env,id.yield)} ','}* ')'
    public static class Parameters extends NonterminalNode {
        public Parameters(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitParameters(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // RegexBody = {Regex+ '|'}*
    public static class RegexBody extends NonterminalNode {
        public RegexBody(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitRegexBody(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Body = {PriorityLevels '>'}+
    public static class Body extends NonterminalNode {
        public Body(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitBody(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // PriorityLevels = {Alternative '|'}+
    public static class PriorityLevels extends NonterminalNode {
        public PriorityLevels(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPriorityLevels(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Alternative extends NonterminalNode {
        public Alternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Alternative = seq:Sequence
    public static class SequenceAlternative extends Alternative {
        public SequenceAlternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Sequence seq() {
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

    // Alternative = assoc:Associativity '(' seqs:{Sequence '|'}+ ')'
    public static class AssociativityAlternative extends Alternative {
        public AssociativityAlternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode assoc() {
           return (TerminalNode) childAt(0);
        }

        public PlusNode seqs() {
           return (PlusNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAssociativityAlternative(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Sequence extends NonterminalNode {
        public Sequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Sequence = assoc:Associativity? cond:Condition? first:Symbol rest:Symbol+ ret:ReturnExpression? label:Label?
    public static class MoreThanOneElemSequence extends Sequence {
        public MoreThanOneElemSequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public OptionNode assoc() {
           return (OptionNode) childAt(0);
        }

        public OptionNode cond() {
           return (OptionNode) childAt(1);
        }

        public Symbol first() {
           return (Symbol) childAt(2);
        }

        public PlusNode rest() {
           return (PlusNode) childAt(3);
        }

        public OptionNode ret() {
           return (OptionNode) childAt(4);
        }

        public OptionNode label() {
           return (OptionNode) childAt(5);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitMoreThanOneElemSequence(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Sequence = cond:Condition? sym:Symbol ret:ReturnExpression? label:Label?
    public static class SingleElemSequence extends Sequence {
        public SingleElemSequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public OptionNode cond() {
           return (OptionNode) childAt(0);
        }

        public Symbol sym() {
           return (Symbol) childAt(1);
        }

        public OptionNode ret() {
           return (OptionNode) childAt(2);
        }

        public OptionNode label() {
           return (OptionNode) childAt(3);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitSingleElemSequence(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Sequence = label:Label?
    public static class EmptySequence extends Sequence {
        public EmptySequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public OptionNode label() {
           return (OptionNode) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitEmptySequence(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Condition = '{' {Expression ','}* '}' '?'
    public static class Condition extends NonterminalNode {
        public Condition(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCondition(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Symbol extends NonterminalNode {
        public Symbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Symbol = id:Identifier args:Arguments
    public static class CallSymbol extends Symbol {
        public CallSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier id() {
           return (Identifier) childAt(0);
        }

        public Arguments args() {
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

    // Symbol = 'offside' sym:Symbol
    public static class OffsideSymbol extends Symbol {
        public OffsideSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
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

    // Symbol = sym:Symbol '*'
    public static class StarSymbol extends Symbol {
        public StarSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStarSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = sym:Symbol '+'
    public static class PlusSymbol extends Symbol {
        public PlusSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPlusSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = sym:Symbol '?'
    public static class OptionSymbol extends Symbol {
        public OptionSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitOptionSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = '(' syms:Symbol+ ')'
    public static class SequenceSymbol extends Symbol {
        public SequenceSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public PlusNode syms() {
           return (PlusNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitSequenceSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = '(' first:Symbol+ rest:('|' Symbol+)+ ')'
    public static class AlternationSymbol extends Symbol {
        public AlternationSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public PlusNode first() {
           return (PlusNode) childAt(1);
        }

        public PlusNode rest() {
           return (PlusNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAlternationSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = 'align' sym:Symbol
    public static class AlignSymbol extends Symbol {
        public AlignSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
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

    // Symbol = 'ignore' sym:Symbol
    public static class IgnoreSymbol extends Symbol {
        public IgnoreSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
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

    // Symbol = id:Identifier ':' sym:Symbol
    public static class LabeledSymbol extends Symbol {
        public LabeledSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier id() {
           return (Identifier) childAt(0);
        }

        public Symbol sym() {
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

    // Symbol = sym:Symbol stmts:Statement+
    public static class StatementSymbol extends Symbol {
        public StatementSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        public PlusNode stmts() {
           return (PlusNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStatementSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = sym:Symbol cond:Condition
    public static class PostConditionSymbol extends Symbol {
        public PostConditionSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        public Condition cond() {
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

    // Symbol = reg:Regex '<<' sym:Symbol
    public static class PrecedeSymbol extends Symbol {
        public PrecedeSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex reg() {
           return (Regex) childAt(0);
        }

        public Symbol sym() {
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

    // Symbol = reg:Regex '!<<' sym:Symbol
    public static class NotPrecedeSymbol extends Symbol {
        public NotPrecedeSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex reg() {
           return (Regex) childAt(0);
        }

        public Symbol sym() {
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

    // Symbol = '^' sym:Symbol
    public static class StartOfLineSymbol extends Symbol {
        public StartOfLineSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStartOfLineSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = sym:Symbol '>>' reg:Regex
    public static class FollowSymbol extends Symbol {
        public FollowSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        public Regex reg() {
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

    // Symbol = sym:Symbol '!>>' reg:Regex
    public static class NotFollowSymbol extends Symbol {
        public NotFollowSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        public Regex reg() {
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

    // Symbol = sym:Symbol '\' reg:Regex
    public static class ExcludeSymbol extends Symbol {
        public ExcludeSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        public Regex reg() {
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

    // Symbol = sym:Symbol '!' id:Identifier
    public static class ExceptSymbol extends Symbol {
        public ExceptSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        public Identifier id() {
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

    // Symbol = sym:Symbol '$'
    public static class EndOfLineSymbol extends Symbol {
        public EndOfLineSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitEndOfLineSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = sym:Symbol '$$'
    public static class EndOfFileSymbol extends Symbol {
        public EndOfFileSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitEndOfFileSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = 'if' exp:Expression thenPart:Symbol 'else' elsePart:Symbol
    public static class IfThenElseSymbol extends Symbol {
        public IfThenElseSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression exp() {
           return (Expression) childAt(1);
        }

        public Symbol thenPart() {
           return (Symbol) childAt(2);
        }

        public Symbol elsePart() {
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

    // Symbol = id:Identifier
    public static class IdentifierSymbol extends Symbol {
        public IdentifierSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier id() {
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

    // Symbol = string:String
    public static class StringSymbol extends Symbol {
        public StringSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode string() {
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

    // Symbol = charClass:CharClass
    public static class CharClassSymbol extends Symbol {
        public CharClassSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public CharClass charClass() {
           return (CharClass) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCharClassSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = '{' sym:Symbol sep:Symbol+ '}' '*'
    public static class StarSepSymbol extends Symbol {
        public StarSepSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(1);
        }

        public PlusNode sep() {
           return (PlusNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStarSepSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Symbol = '{' sym:Symbol sep:Symbol+ '}' '+'
    public static class PlusSepSymbol extends Symbol {
        public PlusSepSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol sym() {
           return (Symbol) childAt(1);
        }

        public PlusNode sep() {
           return (PlusNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPlusSepSymbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Arguments = '(' {Expression ','}* ')'
    public static class Arguments extends NonterminalNode {
        public Arguments(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitArguments(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Statement extends NonterminalNode {
        public Statement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Statement = fun:FunName args:Arguments ';'?
    public static class CallStatement extends Statement {
        public CallStatement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public FunName fun() {
           return (FunName) childAt(0);
        }

        public Arguments args() {
           return (Arguments) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCallStatement(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Statement = bindings:Binding ';'?
    public static class BindingStatement extends Statement {
        public BindingStatement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Binding bindings() {
           return (Binding) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitBindingStatement(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Binding extends NonterminalNode {
        public Binding(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Binding = varName:VarName '=' exp:Expression
    public static class AssignBinding extends Binding {
        public AssignBinding(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public VarName varName() {
           return (VarName) childAt(0);
        }

        public Expression exp() {
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

    // Binding = 'var' decls:{(id:Name {env = put(env,id.yield)} '=' Expression) ','}+
    public static class DeclareBinding extends Binding {
        public DeclareBinding(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public PlusNode decls() {
           return (PlusNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitDeclareBinding(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Regex extends NonterminalNode {
        public Regex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Regex = reg:Regex '*'
    public static class StarRegex extends Regex {
        public StarRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex reg() {
           return (Regex) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitStarRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = reg:Regex '+'
    public static class PlusRegex extends Regex {
        public PlusRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex reg() {
           return (Regex) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPlusRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = reg:Regex '?'
    public static class OptionRegex extends Regex {
        public OptionRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex reg() {
           return (Regex) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitOptionRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = '(' reg:Regex ')'
    public static class BracketRegex extends Regex {
        public BracketRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex reg() {
           return (Regex) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitBracketRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = '(' first:Regex rest:Regex+ ')'
    public static class SequenceRegex extends Regex {
        public SequenceRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Regex first() {
           return (Regex) childAt(1);
        }

        public PlusNode rest() {
           return (PlusNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitSequenceRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = '(' first:Regex+ '|' rest:{Regex+ '|'}+ ')'
    public static class AlternationRegex extends Regex {
        public AlternationRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public PlusNode first() {
           return (PlusNode) childAt(1);
        }

        public PlusNode rest() {
           return (PlusNode) childAt(3);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAlternationRegex(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Regex = name:Name
    public static class NontRegex extends Regex {
        public NontRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Name name() {
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

    // Regex = charClass:CharClass
    public static class CharClassRegex extends Regex {
        public CharClassRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public CharClass charClass() {
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

    // Regex = string:String
    public static class StringRegex extends Regex {
        public StringRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode string() {
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

    public static abstract class CharClass extends NonterminalNode {
        public CharClass(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // CharClass = '[' ranges:Range* ']'
    public static class CharsCharClass extends CharClass {
        public CharsCharClass(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public StarNode ranges() {
           return (StarNode) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitCharsCharClass(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // CharClass = '!' '[' ranges:Range* ']'
    public static class NotCharsCharClass extends CharClass {
        public NotCharsCharClass(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public StarNode ranges() {
           return (StarNode) childAt(2);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitNotCharsCharClass(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    public static abstract class Range extends NonterminalNode {
        public Range(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // Range = first:RangeChar '-' second:RangeChar
    public static class RangeRange extends Range {
        public RangeRange(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode first() {
           return (TerminalNode) childAt(0);
        }

        public TerminalNode second() {
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

    // Range = range:RangeChar
    public static class CharacterRange extends Range {
        public CharacterRange(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode range() {
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

    // Expression = fun:FunName args:Arguments
    public static class CallExpression extends Expression {
        public CallExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public FunName fun() {
           return (FunName) childAt(0);
        }

        public Arguments args() {
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

    // Expression = '!' exp:Expression
    public static class NotExpression extends Expression {
        public NotExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression exp() {
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

    // Expression = lhs:Expression '*' rhs:Expression
    public static class MultiplicationExpression extends Expression {
        public MultiplicationExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '/' rhs:Expression
    public static class DivisionExpression extends Expression {
        public DivisionExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '+' rhs:Expression
    public static class AdditionExpression extends Expression {
        public AdditionExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '-' rhs:Expression
    public static class SubtractionExpression extends Expression {
        public SubtractionExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '>=' rhs:Expression
    public static class GreaterEqExpression extends Expression {
        public GreaterEqExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '<=' rhs:Expression
    public static class LessEqExpression extends Expression {
        public LessEqExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '>' rhs:Expression
    public static class GreaterExpression extends Expression {
        public GreaterExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '<' rhs:Expression
    public static class LessExpression extends Expression {
        public LessExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '==' rhs:Expression
    public static class EqualExpression extends Expression {
        public EqualExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '!=' rhs:Expression
    public static class NotEqualExpression extends Expression {
        public NotEqualExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '&&' rhs:Expression
    public static class AndExpression extends Expression {
        public AndExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = lhs:Expression '||' rhs:Expression
    public static class OrExpression extends Expression {
        public OrExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression lhs() {
           return (Expression) childAt(0);
        }

        public Expression rhs() {
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

    // Expression = id:Identifier '.l'
    public static class LExtentExpression extends Expression {
        public LExtentExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier id() {
           return (Identifier) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitLExtentExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = id:Identifier '.r'
    public static class RExtentExpression extends Expression {
        public RExtentExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier id() {
           return (Identifier) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitRExtentExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = id:Identifier '.yield'
    public static class YieldExpression extends Expression {
        public YieldExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier id() {
           return (Identifier) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitYieldExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = id:Identifier '.val'
    public static class ValExpression extends Expression {
        public ValExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier id() {
           return (Identifier) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitValExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Expression = varName:VarName
    public static class NameExpression extends Expression {
        public NameExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public VarName varName() {
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

    // Expression = number:Number
    public static class NumberExpression extends Expression {
        public NumberExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public TerminalNode number() {
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

    // Expression = '(' exp:Expression ')'
    public static class BracketExpression extends Expression {
        public BracketExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression exp() {
           return (Expression) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitBracketExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // ReturnExpression = '{' exp:Expression '}'
    public static class ReturnExpression extends NonterminalNode {
        public ReturnExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression exp() {
           return (Expression) childAt(1);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitReturnExpression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // VarName = id:Identifier
    public static class VarName extends NonterminalNode {
        public VarName(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
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

    // Name = id:Identifier
    public static class Name extends NonterminalNode {
        public Name(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
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

    public static abstract class FunName extends NonterminalNode {
        public FunName(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

    }

    // FunName = 'println'
    public static class PrintlnFunName extends FunName {
        public PrintlnFunName(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPrintlnFunName(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // FunName = 'indent'
    public static class IndentFunName extends FunName {
        public IndentFunName(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitIndentFunName(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // FunName = 'assert'
    public static class AssertFunName extends FunName {
        public AssertFunName(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitAssertFunName(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // FunName = 'set'
    public static class SetFunName extends FunName {
        public SetFunName(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitSetFunName(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // FunName = 'contains'
    public static class ContainsFunName extends FunName {
        public ContainsFunName(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitContainsFunName(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // FunName = 'put'
    public static class PutFunName extends FunName {
        public PutFunName(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitPutFunName(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Identifier = LetterOrDigits
    public static class Identifier extends NonterminalNode {
        public Identifier(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitIdentifier(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // Label = '%' id:Identifier
    public static class Label extends NonterminalNode {
        public Label(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Identifier id() {
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

    // Layout = (WhiteSpace | SingleLineComment | MultiLineComment)*
    public static class Layout extends NonterminalNode {
        public Layout(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visitLayout(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // $_Symbol = child:Symbol
    public static class $_Symbol extends NonterminalNode {
        public $_Symbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Symbol child() {
           return (Symbol) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visit$_Symbol(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

    // $_Expression = child:Expression
    public static class $_Expression extends NonterminalNode {
        public $_Expression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
            super(rule, children, start, end);
        }

        public Expression child() {
           return (Expression) childAt(0);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<T> visitor) {
            if (visitor instanceof IggyParseTreeVisitor) {
                return ((IggyParseTreeVisitor<T>) visitor).visit$_Expression(this);
            }
            return visitor.visitNonterminalNode(this);
        }
    }

}
