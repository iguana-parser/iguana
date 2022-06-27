// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen;

import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.parsetree.DefaultParseTreeBuilder;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.utils.input.Input;

import java.util.List;

import static org.iguana.iggy.gen.IggyParseTree.*;

public class IggyParseTreeBuilder extends DefaultParseTreeBuilder {

    public IggyParseTreeBuilder(Input input) {
        super(input);
    }

    @Override
    public NonterminalNode nonterminalNode(RuntimeRule rule, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        String name = rule.getHead().getName();
        String label = rule.getLabel();

        switch (name) {
            case "Definition":
                return new Definition(rule, children, leftExtent, rightExtent);
            case "Global":
                return new Global(rule, children, leftExtent, rightExtent);
            case "Rule":
                switch (label) {
                    case "ContextFree":
                        return new ContextFreeRule(rule, children, leftExtent, rightExtent);
                    case "Regex":
                        return new RegexRule(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Parameters":
                return new Parameters(rule, children, leftExtent, rightExtent);
            case "RegexBody":
                return new RegexBody(rule, children, leftExtent, rightExtent);
            case "Body":
                return new Body(rule, children, leftExtent, rightExtent);
            case "PriorityLevels":
                return new PriorityLevels(rule, children, leftExtent, rightExtent);
            case "Alternative":
                switch (label) {
                    case "Sequence":
                        return new SequenceAlternative(rule, children, leftExtent, rightExtent);
                    case "Associativity":
                        return new AssociativityAlternative(rule, children, leftExtent, rightExtent);
                    case "Empty":
                        return new EmptyAlternative(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Sequence":
                switch (label) {
                    case "MoreThanOneElem":
                        return new MoreThanOneElemSequence(rule, children, leftExtent, rightExtent);
                    case "SingleElem":
                        return new SingleElemSequence(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Condition":
                return new Condition(rule, children, leftExtent, rightExtent);
            case "Symbol":
                switch (label) {
                    case "Call":
                        return new CallSymbol(rule, children, leftExtent, rightExtent);
                    case "Offside":
                        return new OffsideSymbol(rule, children, leftExtent, rightExtent);
                    case "Star":
                        return new StarSymbol(rule, children, leftExtent, rightExtent);
                    case "Plus":
                        return new PlusSymbol(rule, children, leftExtent, rightExtent);
                    case "Option":
                        return new OptionSymbol(rule, children, leftExtent, rightExtent);
                    case "Sequence":
                        return new SequenceSymbol(rule, children, leftExtent, rightExtent);
                    case "Alternation":
                        return new AlternationSymbol(rule, children, leftExtent, rightExtent);
                    case "Align":
                        return new AlignSymbol(rule, children, leftExtent, rightExtent);
                    case "Ignore":
                        return new IgnoreSymbol(rule, children, leftExtent, rightExtent);
                    case "Labeled":
                        return new LabeledSymbol(rule, children, leftExtent, rightExtent);
                    case "Statement":
                        return new StatementSymbol(rule, children, leftExtent, rightExtent);
                    case "PostCondition":
                        return new PostConditionSymbol(rule, children, leftExtent, rightExtent);
                    case "Precede":
                        return new PrecedeSymbol(rule, children, leftExtent, rightExtent);
                    case "NotPrecede":
                        return new NotPrecedeSymbol(rule, children, leftExtent, rightExtent);
                    case "Follow":
                        return new FollowSymbol(rule, children, leftExtent, rightExtent);
                    case "NotFollow":
                        return new NotFollowSymbol(rule, children, leftExtent, rightExtent);
                    case "Exclude":
                        return new ExcludeSymbol(rule, children, leftExtent, rightExtent);
                    case "Except":
                        return new ExceptSymbol(rule, children, leftExtent, rightExtent);
                    case "IfThenElse":
                        return new IfThenElseSymbol(rule, children, leftExtent, rightExtent);
                    case "Identifier":
                        return new IdentifierSymbol(rule, children, leftExtent, rightExtent);
                    case "String":
                        return new StringSymbol(rule, children, leftExtent, rightExtent);
                    case "StarSep":
                        return new StarSepSymbol(rule, children, leftExtent, rightExtent);
                    case "PlusSep":
                        return new PlusSepSymbol(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Arguments":
                return new Arguments(rule, children, leftExtent, rightExtent);
            case "Statement":
                switch (label) {
                    case "Call":
                        return new CallStatement(rule, children, leftExtent, rightExtent);
                    case "Binding":
                        return new BindingStatement(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Binding":
                switch (label) {
                    case "Assign":
                        return new AssignBinding(rule, children, leftExtent, rightExtent);
                    case "Declare":
                        return new DeclareBinding(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Regex":
                switch (label) {
                    case "Star":
                        return new StarRegex(rule, children, leftExtent, rightExtent);
                    case "Plus":
                        return new PlusRegex(rule, children, leftExtent, rightExtent);
                    case "Option":
                        return new OptionRegex(rule, children, leftExtent, rightExtent);
                    case "Bracket":
                        return new BracketRegex(rule, children, leftExtent, rightExtent);
                    case "Sequence":
                        return new SequenceRegex(rule, children, leftExtent, rightExtent);
                    case "Alternation":
                        return new AlternationRegex(rule, children, leftExtent, rightExtent);
                    case "Nont":
                        return new NontRegex(rule, children, leftExtent, rightExtent);
                    case "CharClass":
                        return new CharClassRegex(rule, children, leftExtent, rightExtent);
                    case "String":
                        return new StringRegex(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Regexs":
                return new Regexs(rule, children, leftExtent, rightExtent);
            case "CharClass":
                switch (label) {
                    case "Chars":
                        return new CharsCharClass(rule, children, leftExtent, rightExtent);
                    case "NotChars":
                        return new NotCharsCharClass(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Range":
                switch (label) {
                    case "Range":
                        return new RangeRange(rule, children, leftExtent, rightExtent);
                    case "Character":
                        return new CharacterRange(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "Expression":
                switch (label) {
                    case "Call":
                        return new CallExpression(rule, children, leftExtent, rightExtent);
                    case "Not":
                        return new NotExpression(rule, children, leftExtent, rightExtent);
                    case "Multiplication":
                        return new MultiplicationExpression(rule, children, leftExtent, rightExtent);
                    case "Division":
                        return new DivisionExpression(rule, children, leftExtent, rightExtent);
                    case "Addition":
                        return new AdditionExpression(rule, children, leftExtent, rightExtent);
                    case "Subtraction":
                        return new SubtractionExpression(rule, children, leftExtent, rightExtent);
                    case "GreaterEq":
                        return new GreaterEqExpression(rule, children, leftExtent, rightExtent);
                    case "LessEq":
                        return new LessEqExpression(rule, children, leftExtent, rightExtent);
                    case "Greater":
                        return new GreaterExpression(rule, children, leftExtent, rightExtent);
                    case "Less":
                        return new LessExpression(rule, children, leftExtent, rightExtent);
                    case "Equal":
                        return new EqualExpression(rule, children, leftExtent, rightExtent);
                    case "NotEqual":
                        return new NotEqualExpression(rule, children, leftExtent, rightExtent);
                    case "And":
                        return new AndExpression(rule, children, leftExtent, rightExtent);
                    case "Or":
                        return new OrExpression(rule, children, leftExtent, rightExtent);
                    case "LExtent":
                        return new LExtentExpression(rule, children, leftExtent, rightExtent);
                    case "RExtent":
                        return new RExtentExpression(rule, children, leftExtent, rightExtent);
                    case "Yield":
                        return new YieldExpression(rule, children, leftExtent, rightExtent);
                    case "Val":
                        return new ValExpression(rule, children, leftExtent, rightExtent);
                    case "Name":
                        return new NameExpression(rule, children, leftExtent, rightExtent);
                    case "Number":
                        return new NumberExpression(rule, children, leftExtent, rightExtent);
                    case "Bracket":
                        return new BracketExpression(rule, children, leftExtent, rightExtent);
                    default:
                        throw new RuntimeException("Unexpected label:" + label);
                }
            case "ReturnExpression":
                return new ReturnExpression(rule, children, leftExtent, rightExtent);
            case "VarName":
                return new VarName(rule, children, leftExtent, rightExtent);
            case "Label":
                return new Label(rule, children, leftExtent, rightExtent);
            case "Name":
                return new Name(rule, children, leftExtent, rightExtent);
            case "Identifier":
                return new Identifier(rule, children, leftExtent, rightExtent);
            default:
                throw new RuntimeException("Unexpected nonterminal:" + name);
        }
    }
}
