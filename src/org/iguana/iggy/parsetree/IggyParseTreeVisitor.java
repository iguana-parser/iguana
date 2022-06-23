package org.iguana.iggy.parsetree;

import org.iguana.parsetree.ParseTreeVisitor;
import org.iguana.parsetree.NonterminalNode;

import static org.iguana.iggy.parsetree.IggyParseTree.*;
public class IggyParseTreeVisitor<T> implements ParseTreeVisitor<T> {

    @Override
    public T visitNonterminalNode(NonterminalNode node) {
        throw new UnsupportedOperationException();
    }

    public T visitRegexs(Regexs node) {
        // Implement me!!!
        return null;
    }

    public T visitCallSymbol(CallSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitOffsideSymbol(OffsideSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitStarSymbol(StarSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitPlusSymbol(PlusSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitOptionSymbol(OptionSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitSequenceSymbol(SequenceSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitAlternationSymbol(AlternationSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitAlignSymbol(AlignSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitIgnoreSymbol(IgnoreSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitLabeledSymbol(LabeledSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitStatementSymbol(StatementSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitPostConditionSymbol(PostConditionSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitPrecedeSymbol(PrecedeSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitNotPrecedeSymbol(NotPrecedeSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitFollowSymbol(FollowSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitNotFollowSymbol(NotFollowSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitExcludeSymbol(ExcludeSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitExceptSymbol(ExceptSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitIfThenElseSymbol(IfThenElseSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitIdentifierSymbol(IdentifierSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitStringSymbol(StringSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitStarSepSymbol(StarSepSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitPlusSepSymbol(PlusSepSymbol node) {
        // Implement me!!!
        return null;
    }

    public T visitLabel(Label node) {
        // Implement me!!!
        return null;
    }

    public T visitGlobal(Global node) {
        // Implement me!!!
        return null;
    }

    public T visitName(Name node) {
        // Implement me!!!
        return null;
    }

    public T visitRegexBody(RegexBody node) {
        // Implement me!!!
        return null;
    }

    public T visitReturnExpression(ReturnExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitIdentifier(Identifier node) {
        // Implement me!!!
        return null;
    }

    public T visitStarRegex(StarRegex node) {
        // Implement me!!!
        return null;
    }

    public T visitPlusRegex(PlusRegex node) {
        // Implement me!!!
        return null;
    }

    public T visitOptionRegex(OptionRegex node) {
        // Implement me!!!
        return null;
    }

    public T visitBracketRegex(BracketRegex node) {
        // Implement me!!!
        return null;
    }

    public T visitSequenceRegex(SequenceRegex node) {
        // Implement me!!!
        return null;
    }

    public T visitAlternationRegex(AlternationRegex node) {
        // Implement me!!!
        return null;
    }

    public T visitNontRegex(NontRegex node) {
        // Implement me!!!
        return null;
    }

    public T visitCharClassRegex(CharClassRegex node) {
        // Implement me!!!
        return null;
    }

    public T visitStringRegex(StringRegex node) {
        // Implement me!!!
        return null;
    }

    public T visitPriorityLevels(PriorityLevels node) {
        // Implement me!!!
        return null;
    }

    public T visitBody(Body node) {
        // Implement me!!!
        return null;
    }

    public T visitCondition(Condition node) {
        // Implement me!!!
        return null;
    }

    public T visitAssignBinding(AssignBinding node) {
        // Implement me!!!
        return null;
    }

    public T visitDeclareBinding(DeclareBinding node) {
        // Implement me!!!
        return null;
    }

    public T visitRegexSequence(RegexSequence node) {
        // Implement me!!!
        return null;
    }

    public T visitParameters(Parameters node) {
        // Implement me!!!
        return null;
    }

    public T visitSequenceAlternative(SequenceAlternative node) {
        // Implement me!!!
        return null;
    }

    public T visitAssociativityAlternative(AssociativityAlternative node) {
        // Implement me!!!
        return null;
    }

    public T visitEmptyAlternative(EmptyAlternative node) {
        // Implement me!!!
        return null;
    }

    public T visitCallStatement(CallStatement node) {
        // Implement me!!!
        return null;
    }

    public T visitBindingStatement(BindingStatement node) {
        // Implement me!!!
        return null;
    }

    public T visitDefinition(Definition node) {
        // Implement me!!!
        return null;
    }

    public T visitArguments(Arguments node) {
        // Implement me!!!
        return null;
    }

    public T visitCharsCharClass(CharsCharClass node) {
        // Implement me!!!
        return null;
    }

    public T visitNotCharsCharClass(NotCharsCharClass node) {
        // Implement me!!!
        return null;
    }

    public T visitMoreThanOneElemSequence(MoreThanOneElemSequence node) {
        // Implement me!!!
        return null;
    }

    public T visitSingleElemSequence(SingleElemSequence node) {
        // Implement me!!!
        return null;
    }

    public T visitRangeRange(RangeRange node) {
        // Implement me!!!
        return null;
    }

    public T visitCharacterRange(CharacterRange node) {
        // Implement me!!!
        return null;
    }

    public T visitCallExpression(CallExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitNotExpression(NotExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitMultiplicationExpression(MultiplicationExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitDivisionExpression(DivisionExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitAdditionExpression(AdditionExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitSubtractionExpression(SubtractionExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitGreaterEqExpression(GreaterEqExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitLessEqExpression(LessEqExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitGreaterExpression(GreaterExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitLessExpression(LessExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitEqualExpression(EqualExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitNotEqualExpression(NotEqualExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitAndExpression(AndExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitOrExpression(OrExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitLExtentExpression(LExtentExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitRExtentExpression(RExtentExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitYieldExpression(YieldExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitValExpression(ValExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitNameExpression(NameExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitNumberExpression(NumberExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitBracketExpression(BracketExpression node) {
        // Implement me!!!
        return null;
    }

    public T visitContextFreeRule(ContextFreeRule node) {
        // Implement me!!!
        return null;
    }

    public T visitRegexRule(RegexRule node) {
        // Implement me!!!
        return null;
    }

    public T visitVarName(VarName node) {
        // Implement me!!!
        return null;
    }

}
