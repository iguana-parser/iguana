// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen;

import org.iguana.parsetree.ParseTreeVisitor;
import org.iguana.parsetree.NonterminalNode;

import static org.iguana.iggy.gen.IggyParseTree.*;

public interface IggyParseTreeVisitor<T> extends ParseTreeVisitor<T> {

    @Override
    default T visitNonterminalNode(NonterminalNode node) {
        throw new UnsupportedOperationException();
    }

    T visitGrammar(Grammar node);

    T visitTopLevelVar(TopLevelVar node);

    T visitContextFreeRule(ContextFreeRule node);

    T visitRegexRule(RegexRule node);

    T visitParameters(Parameters node);

    T visitRegexBody(RegexBody node);

    T visitBody(Body node);

    T visitPriorityLevels(PriorityLevels node);

    T visitSequenceAlternative(SequenceAlternative node);

    T visitAssociativityAlternative(AssociativityAlternative node);

    T visitEmptyAlternative(EmptyAlternative node);

    T visitMoreThanOneElemSequence(MoreThanOneElemSequence node);

    T visitSingleElemSequence(SingleElemSequence node);

    T visitCondition(Condition node);

    T visitCallSymbol(CallSymbol node);

    T visitOffsideSymbol(OffsideSymbol node);

    T visitStarSymbol(StarSymbol node);

    T visitPlusSymbol(PlusSymbol node);

    T visitOptionSymbol(OptionSymbol node);

    T visitSequenceSymbol(SequenceSymbol node);

    T visitAlternationSymbol(AlternationSymbol node);

    T visitAlignSymbol(AlignSymbol node);

    T visitIgnoreSymbol(IgnoreSymbol node);

    T visitLabeledSymbol(LabeledSymbol node);

    T visitStatementSymbol(StatementSymbol node);

    T visitPostConditionSymbol(PostConditionSymbol node);

    T visitPrecedeSymbol(PrecedeSymbol node);

    T visitNotPrecedeSymbol(NotPrecedeSymbol node);

    T visitStartOfLineSymbol(StartOfLineSymbol node);

    T visitFollowSymbol(FollowSymbol node);

    T visitNotFollowSymbol(NotFollowSymbol node);

    T visitExcludeSymbol(ExcludeSymbol node);

    T visitExceptSymbol(ExceptSymbol node);

    T visitEndOfLineSymbol(EndOfLineSymbol node);

    T visitEndOfFileSymbol(EndOfFileSymbol node);

    T visitIfThenElseSymbol(IfThenElseSymbol node);

    T visitIdentifierSymbol(IdentifierSymbol node);

    T visitStringSymbol(StringSymbol node);

    T visitCharClassSymbol(CharClassSymbol node);

    T visitStarSepSymbol(StarSepSymbol node);

    T visitPlusSepSymbol(PlusSepSymbol node);

    T visitArguments(Arguments node);

    T visitCallStatement(CallStatement node);

    T visitBindingStatement(BindingStatement node);

    T visitAssignBinding(AssignBinding node);

    T visitDeclareBinding(DeclareBinding node);

    T visitStarRegex(StarRegex node);

    T visitPlusRegex(PlusRegex node);

    T visitOptionRegex(OptionRegex node);

    T visitBracketRegex(BracketRegex node);

    T visitSequenceRegex(SequenceRegex node);

    T visitAlternationRegex(AlternationRegex node);

    T visitNontRegex(NontRegex node);

    T visitCharClassRegex(CharClassRegex node);

    T visitStringRegex(StringRegex node);

    T visitCharsCharClass(CharsCharClass node);

    T visitNotCharsCharClass(NotCharsCharClass node);

    T visitRangeRange(RangeRange node);

    T visitCharacterRange(CharacterRange node);

    T visitCallExpression(CallExpression node);

    T visitNotExpression(NotExpression node);

    T visitMultiplicationExpression(MultiplicationExpression node);

    T visitDivisionExpression(DivisionExpression node);

    T visitAdditionExpression(AdditionExpression node);

    T visitSubtractionExpression(SubtractionExpression node);

    T visitGreaterEqExpression(GreaterEqExpression node);

    T visitLessEqExpression(LessEqExpression node);

    T visitGreaterExpression(GreaterExpression node);

    T visitLessExpression(LessExpression node);

    T visitEqualExpression(EqualExpression node);

    T visitNotEqualExpression(NotEqualExpression node);

    T visitAndExpression(AndExpression node);

    T visitOrExpression(OrExpression node);

    T visitLExtentExpression(LExtentExpression node);

    T visitRExtentExpression(RExtentExpression node);

    T visitYieldExpression(YieldExpression node);

    T visitValExpression(ValExpression node);

    T visitNameExpression(NameExpression node);

    T visitNumberExpression(NumberExpression node);

    T visitBracketExpression(BracketExpression node);

    T visitReturnExpression(ReturnExpression node);

    T visitVarName(VarName node);

    T visitName(Name node);

    T visitIdentifier(Identifier node);

    T visitLabel(Label node);

}
