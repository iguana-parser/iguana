// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen;

import org.iguana.parsetree.ParseTreeVisitor;
import org.iguana.parsetree.NonterminalNode;

import static org.iguana.iggy.gen.IggyParseTree.*;

public abstract class IggyParseTreeVisitor<T> implements ParseTreeVisitor<T> {

    @Override
    public T visitNonterminalNode(NonterminalNode node) {
        throw new UnsupportedOperationException();
    }

    public abstract T visitGrammar(Grammar node);

    public abstract T visitTopLevelVar(TopLevelVar node);

    public abstract T visitContextFreeRule(ContextFreeRule node);

    public abstract T visitRegexRule(RegexRule node);

    public abstract T visitParameters(Parameters node);

    public abstract T visitRegexBody(RegexBody node);

    public abstract T visitBody(Body node);

    public abstract T visitPriorityLevels(PriorityLevels node);

    public abstract T visitSequenceAlternative(SequenceAlternative node);

    public abstract T visitAssociativityAlternative(AssociativityAlternative node);

    public abstract T visitEmptyAlternative(EmptyAlternative node);

    public abstract T visitMoreThanOneElemSequence(MoreThanOneElemSequence node);

    public abstract T visitSingleElemSequence(SingleElemSequence node);

    public abstract T visitCondition(Condition node);

    public abstract T visitCallSymbol(CallSymbol node);

    public abstract T visitOffsideSymbol(OffsideSymbol node);

    public abstract T visitStarSymbol(StarSymbol node);

    public abstract T visitPlusSymbol(PlusSymbol node);

    public abstract T visitOptionSymbol(OptionSymbol node);

    public abstract T visitSequenceSymbol(SequenceSymbol node);

    public abstract T visitAlternationSymbol(AlternationSymbol node);

    public abstract T visitAlignSymbol(AlignSymbol node);

    public abstract T visitIgnoreSymbol(IgnoreSymbol node);

    public abstract T visitLabeledSymbol(LabeledSymbol node);

    public abstract T visitStatementSymbol(StatementSymbol node);

    public abstract T visitPostConditionSymbol(PostConditionSymbol node);

    public abstract T visitPrecedeSymbol(PrecedeSymbol node);

    public abstract T visitNotPrecedeSymbol(NotPrecedeSymbol node);

    public abstract T visitStartOfLineSymbol(StartOfLineSymbol node);

    public abstract T visitFollowSymbol(FollowSymbol node);

    public abstract T visitNotFollowSymbol(NotFollowSymbol node);

    public abstract T visitExcludeSymbol(ExcludeSymbol node);

    public abstract T visitExceptSymbol(ExceptSymbol node);

    public abstract T visitEndOfLineSymbol(EndOfLineSymbol node);

    public abstract T visitEndOfFileSymbol(EndOfFileSymbol node);

    public abstract T visitIfThenElseSymbol(IfThenElseSymbol node);

    public abstract T visitIdentifierSymbol(IdentifierSymbol node);

    public abstract T visitStringSymbol(StringSymbol node);

    public abstract T visitCharClassSymbol(CharClassSymbol node);

    public abstract T visitStarSepSymbol(StarSepSymbol node);

    public abstract T visitPlusSepSymbol(PlusSepSymbol node);

    public abstract T visitArguments(Arguments node);

    public abstract T visitCallStatement(CallStatement node);

    public abstract T visitBindingStatement(BindingStatement node);

    public abstract T visitAssignBinding(AssignBinding node);

    public abstract T visitDeclareBinding(DeclareBinding node);

    public abstract T visitStarRegex(StarRegex node);

    public abstract T visitPlusRegex(PlusRegex node);

    public abstract T visitOptionRegex(OptionRegex node);

    public abstract T visitBracketRegex(BracketRegex node);

    public abstract T visitSequenceRegex(SequenceRegex node);

    public abstract T visitAlternationRegex(AlternationRegex node);

    public abstract T visitNontRegex(NontRegex node);

    public abstract T visitCharClassRegex(CharClassRegex node);

    public abstract T visitStringRegex(StringRegex node);

    public abstract T visitCharsCharClass(CharsCharClass node);

    public abstract T visitNotCharsCharClass(NotCharsCharClass node);

    public abstract T visitRangeRange(RangeRange node);

    public abstract T visitCharacterRange(CharacterRange node);

    public abstract T visitCallExpression(CallExpression node);

    public abstract T visitNotExpression(NotExpression node);

    public abstract T visitMultiplicationExpression(MultiplicationExpression node);

    public abstract T visitDivisionExpression(DivisionExpression node);

    public abstract T visitAdditionExpression(AdditionExpression node);

    public abstract T visitSubtractionExpression(SubtractionExpression node);

    public abstract T visitGreaterEqExpression(GreaterEqExpression node);

    public abstract T visitLessEqExpression(LessEqExpression node);

    public abstract T visitGreaterExpression(GreaterExpression node);

    public abstract T visitLessExpression(LessExpression node);

    public abstract T visitEqualExpression(EqualExpression node);

    public abstract T visitNotEqualExpression(NotEqualExpression node);

    public abstract T visitAndExpression(AndExpression node);

    public abstract T visitOrExpression(OrExpression node);

    public abstract T visitLExtentExpression(LExtentExpression node);

    public abstract T visitRExtentExpression(RExtentExpression node);

    public abstract T visitYieldExpression(YieldExpression node);

    public abstract T visitValExpression(ValExpression node);

    public abstract T visitNameExpression(NameExpression node);

    public abstract T visitNumberExpression(NumberExpression node);

    public abstract T visitBracketExpression(BracketExpression node);

    public abstract T visitReturnExpression(ReturnExpression node);

    public abstract T visitVarName(VarName node);

    public abstract T visitName(Name node);

    public abstract T visitIdentifier(Identifier node);

    public abstract T visitLabel(Label node);

}
