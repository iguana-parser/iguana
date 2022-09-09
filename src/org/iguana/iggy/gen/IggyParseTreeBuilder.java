// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen;

import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.parsetree.DefaultParseTreeBuilder;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.utils.input.Input;

import java.util.List;

public class IggyParseTreeBuilder extends DefaultParseTreeBuilder {

    public IggyParseTreeBuilder(Input input) {
        super(input);
    }

    @Override
    public NonterminalNode nonterminalNode(RuntimeRule rule, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        java.lang.String name = rule.getHead().getName();
        java.lang.String label = rule.getLabel();

        switch (name) {
            case "Grammar":
                return new IggyParseTree.Grammar(rule, children, leftExtent, rightExtent);
            case "TopLevelVar":
                return new IggyParseTree.TopLevelVar(rule, children, leftExtent, rightExtent);
            case "Rule":
                switch (label) {
                    case "ContextFree":
                        return new IggyParseTree.ContextFreeRule(rule, children, leftExtent, rightExtent);
                    case "Regex":
                        return new IggyParseTree.RegexRule(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Parameters":
                return new IggyParseTree.Parameters(rule, children, leftExtent, rightExtent);
            case "Annotation":
                return new IggyParseTree.Annotation(rule, children, leftExtent, rightExtent);
            case "RegexBody":
                return new IggyParseTree.RegexBody(rule, children, leftExtent, rightExtent);
            case "Body":
                return new IggyParseTree.Body(rule, children, leftExtent, rightExtent);
            case "PriorityLevels":
                return new IggyParseTree.PriorityLevels(rule, children, leftExtent, rightExtent);
            case "Alternative":
                switch (label) {
                    case "Sequence":
                        return new IggyParseTree.SequenceAlternative(rule, children, leftExtent, rightExtent);
                    case "Associativity":
                        return new IggyParseTree.AssociativityAlternative(rule, children, leftExtent, rightExtent);
                    case "Empty":
                        return new IggyParseTree.EmptyAlternative(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Sequence":
                switch (label) {
                    case "MoreThanOneElem":
                        return new IggyParseTree.MoreThanOneElemSequence(rule, children, leftExtent, rightExtent);
                    case "SingleElem":
                        return new IggyParseTree.SingleElemSequence(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Condition":
                return new IggyParseTree.Condition(rule, children, leftExtent, rightExtent);
            case "Symbol":
                switch (label) {
                    case "Call":
                        return new IggyParseTree.CallSymbol(rule, children, leftExtent, rightExtent);
                    case "Offside":
                        return new IggyParseTree.OffsideSymbol(rule, children, leftExtent, rightExtent);
                    case "Star":
                        return new IggyParseTree.StarSymbol(rule, children, leftExtent, rightExtent);
                    case "Plus":
                        return new IggyParseTree.PlusSymbol(rule, children, leftExtent, rightExtent);
                    case "Option":
                        return new IggyParseTree.OptionSymbol(rule, children, leftExtent, rightExtent);
                    case "Sequence":
                        return new IggyParseTree.SequenceSymbol(rule, children, leftExtent, rightExtent);
                    case "Alternation":
                        return new IggyParseTree.AlternationSymbol(rule, children, leftExtent, rightExtent);
                    case "Align":
                        return new IggyParseTree.AlignSymbol(rule, children, leftExtent, rightExtent);
                    case "Ignore":
                        return new IggyParseTree.IgnoreSymbol(rule, children, leftExtent, rightExtent);
                    case "Labeled":
                        return new IggyParseTree.LabeledSymbol(rule, children, leftExtent, rightExtent);
                    case "Statement":
                        return new IggyParseTree.StatementSymbol(rule, children, leftExtent, rightExtent);
                    case "PostCondition":
                        return new IggyParseTree.PostConditionSymbol(rule, children, leftExtent, rightExtent);
                    case "Precede":
                        return new IggyParseTree.PrecedeSymbol(rule, children, leftExtent, rightExtent);
                    case "NotPrecede":
                        return new IggyParseTree.NotPrecedeSymbol(rule, children, leftExtent, rightExtent);
                    case "StartOfLine":
                        return new IggyParseTree.StartOfLineSymbol(rule, children, leftExtent, rightExtent);
                    case "Follow":
                        return new IggyParseTree.FollowSymbol(rule, children, leftExtent, rightExtent);
                    case "NotFollow":
                        return new IggyParseTree.NotFollowSymbol(rule, children, leftExtent, rightExtent);
                    case "Exclude":
                        return new IggyParseTree.ExcludeSymbol(rule, children, leftExtent, rightExtent);
                    case "Except":
                        return new IggyParseTree.ExceptSymbol(rule, children, leftExtent, rightExtent);
                    case "EndOfLine":
                        return new IggyParseTree.EndOfLineSymbol(rule, children, leftExtent, rightExtent);
                    case "EndOfFile":
                        return new IggyParseTree.EndOfFileSymbol(rule, children, leftExtent, rightExtent);
                    case "IfThenElse":
                        return new IggyParseTree.IfThenElseSymbol(rule, children, leftExtent, rightExtent);
                    case "Identifier":
                        return new IggyParseTree.IdentifierSymbol(rule, children, leftExtent, rightExtent);
                    case "String":
                        return new IggyParseTree.StringSymbol(rule, children, leftExtent, rightExtent);
                    case "CharClass":
                        return new IggyParseTree.CharClassSymbol(rule, children, leftExtent, rightExtent);
                    case "StarSep":
                        return new IggyParseTree.StarSepSymbol(rule, children, leftExtent, rightExtent);
                    case "PlusSep":
                        return new IggyParseTree.PlusSepSymbol(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Arguments":
                return new IggyParseTree.Arguments(rule, children, leftExtent, rightExtent);
            case "Statement":
                switch (label) {
                    case "Call":
                        return new IggyParseTree.CallStatement(rule, children, leftExtent, rightExtent);
                    case "Binding":
                        return new IggyParseTree.BindingStatement(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Binding":
                switch (label) {
                    case "Assign":
                        return new IggyParseTree.AssignBinding(rule, children, leftExtent, rightExtent);
                    case "Declare":
                        return new IggyParseTree.DeclareBinding(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Regex":
                switch (label) {
                    case "Star":
                        return new IggyParseTree.StarRegex(rule, children, leftExtent, rightExtent);
                    case "Plus":
                        return new IggyParseTree.PlusRegex(rule, children, leftExtent, rightExtent);
                    case "Option":
                        return new IggyParseTree.OptionRegex(rule, children, leftExtent, rightExtent);
                    case "Bracket":
                        return new IggyParseTree.BracketRegex(rule, children, leftExtent, rightExtent);
                    case "Sequence":
                        return new IggyParseTree.SequenceRegex(rule, children, leftExtent, rightExtent);
                    case "Alternation":
                        return new IggyParseTree.AlternationRegex(rule, children, leftExtent, rightExtent);
                    case "Nont":
                        return new IggyParseTree.NontRegex(rule, children, leftExtent, rightExtent);
                    case "CharClass":
                        return new IggyParseTree.CharClassRegex(rule, children, leftExtent, rightExtent);
                    case "String":
                        return new IggyParseTree.StringRegex(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "CharClass":
                switch (label) {
                    case "Chars":
                        return new IggyParseTree.CharsCharClass(rule, children, leftExtent, rightExtent);
                    case "NotChars":
                        return new IggyParseTree.NotCharsCharClass(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Range":
                switch (label) {
                    case "Range":
                        return new IggyParseTree.RangeRange(rule, children, leftExtent, rightExtent);
                    case "Character":
                        return new IggyParseTree.CharacterRange(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Expression":
                switch (label) {
                    case "Call":
                        return new IggyParseTree.CallExpression(rule, children, leftExtent, rightExtent);
                    case "Not":
                        return new IggyParseTree.NotExpression(rule, children, leftExtent, rightExtent);
                    case "Multiplication":
                        return new IggyParseTree.MultiplicationExpression(rule, children, leftExtent, rightExtent);
                    case "Division":
                        return new IggyParseTree.DivisionExpression(rule, children, leftExtent, rightExtent);
                    case "Addition":
                        return new IggyParseTree.AdditionExpression(rule, children, leftExtent, rightExtent);
                    case "Subtraction":
                        return new IggyParseTree.SubtractionExpression(rule, children, leftExtent, rightExtent);
                    case "GreaterEq":
                        return new IggyParseTree.GreaterEqExpression(rule, children, leftExtent, rightExtent);
                    case "LessEq":
                        return new IggyParseTree.LessEqExpression(rule, children, leftExtent, rightExtent);
                    case "Greater":
                        return new IggyParseTree.GreaterExpression(rule, children, leftExtent, rightExtent);
                    case "Less":
                        return new IggyParseTree.LessExpression(rule, children, leftExtent, rightExtent);
                    case "Equal":
                        return new IggyParseTree.EqualExpression(rule, children, leftExtent, rightExtent);
                    case "NotEqual":
                        return new IggyParseTree.NotEqualExpression(rule, children, leftExtent, rightExtent);
                    case "And":
                        return new IggyParseTree.AndExpression(rule, children, leftExtent, rightExtent);
                    case "Or":
                        return new IggyParseTree.OrExpression(rule, children, leftExtent, rightExtent);
                    case "LExtent":
                        return new IggyParseTree.LExtentExpression(rule, children, leftExtent, rightExtent);
                    case "RExtent":
                        return new IggyParseTree.RExtentExpression(rule, children, leftExtent, rightExtent);
                    case "Yield":
                        return new IggyParseTree.YieldExpression(rule, children, leftExtent, rightExtent);
                    case "Val":
                        return new IggyParseTree.ValExpression(rule, children, leftExtent, rightExtent);
                    case "Name":
                        return new IggyParseTree.NameExpression(rule, children, leftExtent, rightExtent);
                    case "Number":
                        return new IggyParseTree.NumberExpression(rule, children, leftExtent, rightExtent);
                    case "Bracket":
                        return new IggyParseTree.BracketExpression(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "ReturnExpression":
                return new IggyParseTree.ReturnExpression(rule, children, leftExtent, rightExtent);
            case "VarName":
                return new IggyParseTree.VarName(rule, children, leftExtent, rightExtent);
            case "Name":
                return new IggyParseTree.Name(rule, children, leftExtent, rightExtent);
            case "FunName":
                switch (label) {
                    case "Println":
                        return new IggyParseTree.PrintlnFunName(rule, children, leftExtent, rightExtent);
                    case "Indent":
                        return new IggyParseTree.IndentFunName(rule, children, leftExtent, rightExtent);
                    case "Assert":
                        return new IggyParseTree.AssertFunName(rule, children, leftExtent, rightExtent);
                    case "Set":
                        return new IggyParseTree.SetFunName(rule, children, leftExtent, rightExtent);
                    case "Contains":
                        return new IggyParseTree.ContainsFunName(rule, children, leftExtent, rightExtent);
                    case "Put":
                        return new IggyParseTree.PutFunName(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Identifier":
                return new IggyParseTree.Identifier(rule, children, leftExtent, rightExtent);
            case "Label":
                return new IggyParseTree.Label(rule, children, leftExtent, rightExtent);
            case "Layout":
                return new IggyParseTree.Layout(rule, children, leftExtent, rightExtent);
            default:
                throw new RuntimeException("Unexpected nonterminal:" + name);
        }
    }
}
