// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen;

import org.iguana.parsetree.ParseTreeVisitor;
import org.iguana.parsetree.NonterminalNode;

public interface IggyParseTreeVisitor<T> extends ParseTreeVisitor<T> {

    @Override
    default T visitNonterminalNode(NonterminalNode node) {
        throw new UnsupportedOperationException();
    }

    T visitGrammar(IggyParseTree.Grammar node);

    T visitTopLevelVar(IggyParseTree.TopLevelVar node);

    T visitContextFreeRule(IggyParseTree.ContextFreeRule node);

    T visitRegexRule(IggyParseTree.RegexRule node);

    T visitParameters(IggyParseTree.Parameters node);

    T visitRegexBody(IggyParseTree.RegexBody node);

    T visitBody(IggyParseTree.Body node);

    T visitPriorityLevels(IggyParseTree.PriorityLevels node);

    T visitSequenceAlternative(IggyParseTree.SequenceAlternative node);

    T visitAssociativityAlternative(IggyParseTree.AssociativityAlternative node);

    T visitMoreThanOneElemSequence(IggyParseTree.MoreThanOneElemSequence node);

    T visitSingleElemSequence(IggyParseTree.SingleElemSequence node);

    T visitEmptySequence(IggyParseTree.EmptySequence node);

    T visitCondition(IggyParseTree.Condition node);

    T visitCallSymbol(IggyParseTree.CallSymbol node);

    T visitOffsideSymbol(IggyParseTree.OffsideSymbol node);

    T visitStarSymbol(IggyParseTree.StarSymbol node);

    T visitPlusSymbol(IggyParseTree.PlusSymbol node);

    T visitOptionSymbol(IggyParseTree.OptionSymbol node);

    T visitSequenceSymbol(IggyParseTree.SequenceSymbol node);

    T visitAlternationSymbol(IggyParseTree.AlternationSymbol node);

    T visitAlignSymbol(IggyParseTree.AlignSymbol node);

    T visitIgnoreSymbol(IggyParseTree.IgnoreSymbol node);

    T visitLabeledSymbol(IggyParseTree.LabeledSymbol node);

    T visitStatementSymbol(IggyParseTree.StatementSymbol node);

    T visitPostConditionSymbol(IggyParseTree.PostConditionSymbol node);

    T visitPrecedeSymbol(IggyParseTree.PrecedeSymbol node);

    T visitNotPrecedeSymbol(IggyParseTree.NotPrecedeSymbol node);

    T visitStartOfLineSymbol(IggyParseTree.StartOfLineSymbol node);

    T visitFollowSymbol(IggyParseTree.FollowSymbol node);

    T visitNotFollowSymbol(IggyParseTree.NotFollowSymbol node);

    T visitExcludeSymbol(IggyParseTree.ExcludeSymbol node);

    T visitExceptSymbol(IggyParseTree.ExceptSymbol node);

    T visitEndOfLineSymbol(IggyParseTree.EndOfLineSymbol node);

    T visitEndOfFileSymbol(IggyParseTree.EndOfFileSymbol node);

    T visitIfThenElseSymbol(IggyParseTree.IfThenElseSymbol node);

    T visitIdentifierSymbol(IggyParseTree.IdentifierSymbol node);

    T visitStringSymbol(IggyParseTree.StringSymbol node);

    T visitCharClassSymbol(IggyParseTree.CharClassSymbol node);

    T visitStarSepSymbol(IggyParseTree.StarSepSymbol node);

    T visitPlusSepSymbol(IggyParseTree.PlusSepSymbol node);

    T visitErrorSymbol(IggyParseTree.ErrorSymbol node);

    T visitArguments(IggyParseTree.Arguments node);

    T visitCallStatement(IggyParseTree.CallStatement node);

    T visitBindingStatement(IggyParseTree.BindingStatement node);

    T visitAssignBinding(IggyParseTree.AssignBinding node);

    T visitDeclareBinding(IggyParseTree.DeclareBinding node);

    T visitStarRegex(IggyParseTree.StarRegex node);

    T visitPlusRegex(IggyParseTree.PlusRegex node);

    T visitOptionRegex(IggyParseTree.OptionRegex node);

    T visitBracketRegex(IggyParseTree.BracketRegex node);

    T visitSequenceRegex(IggyParseTree.SequenceRegex node);

    T visitAlternationRegex(IggyParseTree.AlternationRegex node);

    T visitNontRegex(IggyParseTree.NontRegex node);

    T visitCharClassRegex(IggyParseTree.CharClassRegex node);

    T visitStringRegex(IggyParseTree.StringRegex node);

    T visitCharsCharClass(IggyParseTree.CharsCharClass node);

    T visitNotCharsCharClass(IggyParseTree.NotCharsCharClass node);

    T visitRangeRange(IggyParseTree.RangeRange node);

    T visitCharacterRange(IggyParseTree.CharacterRange node);

    T visitCallExpression(IggyParseTree.CallExpression node);

    T visitNotExpression(IggyParseTree.NotExpression node);

    T visitMultiplicationExpression(IggyParseTree.MultiplicationExpression node);

    T visitDivisionExpression(IggyParseTree.DivisionExpression node);

    T visitAdditionExpression(IggyParseTree.AdditionExpression node);

    T visitSubtractionExpression(IggyParseTree.SubtractionExpression node);

    T visitGreaterEqExpression(IggyParseTree.GreaterEqExpression node);

    T visitLessEqExpression(IggyParseTree.LessEqExpression node);

    T visitGreaterExpression(IggyParseTree.GreaterExpression node);

    T visitLessExpression(IggyParseTree.LessExpression node);

    T visitEqualExpression(IggyParseTree.EqualExpression node);

    T visitNotEqualExpression(IggyParseTree.NotEqualExpression node);

    T visitAndExpression(IggyParseTree.AndExpression node);

    T visitOrExpression(IggyParseTree.OrExpression node);

    T visitLExtentExpression(IggyParseTree.LExtentExpression node);

    T visitRExtentExpression(IggyParseTree.RExtentExpression node);

    T visitYieldExpression(IggyParseTree.YieldExpression node);

    T visitValExpression(IggyParseTree.ValExpression node);

    T visitNameExpression(IggyParseTree.NameExpression node);

    T visitNumberExpression(IggyParseTree.NumberExpression node);

    T visitBracketExpression(IggyParseTree.BracketExpression node);

    T visitReturnExpression(IggyParseTree.ReturnExpression node);

    T visitVarName(IggyParseTree.VarName node);

    T visitName(IggyParseTree.Name node);

    T visitPrintlnFunName(IggyParseTree.PrintlnFunName node);

    T visitIndentFunName(IggyParseTree.IndentFunName node);

    T visitAssertFunName(IggyParseTree.AssertFunName node);

    T visitSetFunName(IggyParseTree.SetFunName node);

    T visitContainsFunName(IggyParseTree.ContainsFunName node);

    T visitPutFunName(IggyParseTree.PutFunName node);

    T visitIdentifier(IggyParseTree.Identifier node);

    T visitLabel(IggyParseTree.Label node);

    T visitNoNLLayout(IggyParseTree.NoNLLayout node);

    T visitLayout(IggyParseTree.Layout node);

    default T visit$_Symbol(IggyParseTree.$_Symbol node) {
        return node.child().accept(this);
    }

    default T visit$_Expression(IggyParseTree.$_Expression node) {
        return node.child().accept(this);
    }

}
