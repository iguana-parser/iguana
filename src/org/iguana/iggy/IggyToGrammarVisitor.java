package org.iguana.iggy;

import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.*;
import org.iguana.iggy.gen.IggyParseTree;
import org.iguana.iggy.gen.IggyParseTree.RegexRule;
import org.iguana.iggy.gen.IggyParseTreeVisitor;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.regex.Char;
import org.iguana.regex.CharRange;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Seq;

import java.util.*;
import java.util.stream.Collectors;

import static org.iguana.utils.collections.CollectionsUtil.flatten;

public class IggyToGrammarVisitor extends IggyParseTreeVisitor<Object> {

    private final Map<String, RegularExpression> regularExpressionMap = new LinkedHashMap<>();
    private final Map<String, RegularExpression> literals = new LinkedHashMap<>();

    private String start;
    private org.iguana.grammar.symbol.Identifier layout;
    private final Map<String, Expression> globals = new HashMap<>();

    @Override
    public Grammar visitDefinition(IggyParseTree.Definition node) {
        Grammar.Builder builder = new Grammar.Builder();
        List<Rule> rules = (List<Rule>) node.child0().accept(this);
        for (Rule rule : rules) {
            if (rule != null) { // null means a global definition
                builder.addRule(rule);
            }
        }
        for (Map.Entry<String, RegularExpression> entry : regularExpressionMap.entrySet()) {
            builder.addRegularExpression(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Expression> entry : globals.entrySet()) {
            builder.addGlobal(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, RegularExpression> entry : literals.entrySet()) {
            builder.addLiteral(entry.getKey(), entry.getValue());
        }
        builder.setStartSymbol(Start.from(start));
        builder.setLayout(layout);
        return builder.build();
    }

    @Override
    public Void visitGlobal(IggyParseTree.Global node) {
        String key = node.child1().getText();
        Expression value = (Expression) node.childAt(3).accept(this);
        globals.put(key, value);
        return null;
    }

    @Override
    public Object visitContextFreeRule(IggyParseTree.ContextFreeRule node) {
        Identifier nonterminalName = getIdentifier(node.childAt(1));
        Optional<List<Identifier>> parameters = (Optional<List<Identifier>>) node.params().accept(this);
        List<PriorityLevel> priorityLevels = (List<PriorityLevel>) node.body().accept(this);

        if (!node.child0().children().isEmpty()) { // start symbol
            String text = node.child0().getText();
            if (text.equals("start")) {
                start = nonterminalName.getName();
            } else { // "layout"
                layout = nonterminalName;
            }
        }

        Nonterminal nonterminal = new Nonterminal.Builder(nonterminalName.getName()).addParameters(!parameters.isPresent() ? Collections.emptyList() : parameters.get().stream().map(AbstractSymbol::toString).collect(Collectors.toList())).build();
        return new Rule.Builder(nonterminal).addPriorityLevels(priorityLevels).build();
    }

    @Override
    public Void visitRegexRule(RegexRule node) {
        List<List<RegularExpression>> alts = (List<List<RegularExpression>>) node.child4().accept(this);
        Identifier identifier = getIdentifier(node.childAt(2));
        regularExpressionMap.put(identifier.getName(), getRegex(alts));

        if (!node.childAt(0).children().isEmpty()) {
            layout = identifier;
        }

        return null;
    }

    @Override
    public List<Identifier> visitParameters(IggyParseTree.Parameters node) {
        return (List<Identifier>) visitChildren(node);
    }

    @Override
    public Object visitRegexBody(IggyParseTree.RegexBody node) {
        return visitChildren(node);
    }

    @Override
    public Object visitBody(IggyParseTree.Body node) {
        return visitChildren(node);
    }

    @Override
    public PriorityLevel visitPriorityLevels(IggyParseTree.PriorityLevels node) {
        PriorityLevel.Builder builder = new PriorityLevel.Builder();
        builder.addAlternatives((List<Alternative>) node.child0().accept(this));
        return builder.build();
    }

    @Override
    public Alternative visitSequenceAlternative(IggyParseTree.SequenceAlternative node) {
        Alternative.Builder builder = new Alternative.Builder();
        builder.addSequence((Sequence) node.child0().accept(this));
        return builder.build();
    }

    @Override
    public Object visitAssociativityAlternative(IggyParseTree.AssociativityAlternative node) {
        Associativity associativity = getAssociativity(node.childAt(0));
        List<Sequence> seqs = new ArrayList<>();
        seqs.add((Sequence) node.childAt(2).accept(this));
        seqs.addAll((List<Sequence>) node.childAt(3).accept(this));
        return new Alternative.Builder(seqs, associativity).build();
    }

    @Override
    public Object visitEmptyAlternative(IggyParseTree.EmptyAlternative node) {
        Optional<String> label = (Optional<String>) node.child0().accept(this);
        if (label.isPresent()) {
            Sequence sequence = new Sequence.Builder().setLabel(label.get()).build();
            return new Alternative.Builder().addSequence(sequence).build();
        } else {
            return new Alternative.Builder().build();
        }
    }

    @Override
    public Object visitMoreThanOneElemSequence(IggyParseTree.MoreThanOneElemSequence node) {
        Associativity associativity = null;
        if (!node.child0().children().isEmpty()) {
            associativity = getAssociativity(node.child0().childAt(0));
        }
        Sequence.Builder builder = new Sequence.Builder();
        Optional<List<Expression>> expressions = (Optional<List<Expression>>) node.child1().accept(this);
        List<Symbol> symbols = new ArrayList<>();
        Symbol symbol = (Symbol) node.childAt(2).accept(this);
        SymbolBuilder<? extends Symbol> symbolBuilder = symbol.copy();
        if (expressions.isPresent()) {
            for (Expression expression : expressions.get()) {
                symbolBuilder.addPreCondition(DataDependentCondition.predicate(expression));
            }
        }
        symbols.add(symbolBuilder.build());
        symbols.addAll((List<Symbol>) node.child3().accept(this));
        builder.addSymbols(symbols);
        Optional<Expression> returnExpression = (Optional<Expression>) node.child4().accept(this);
        if (returnExpression.isPresent()) {
            builder.addSymbol(Return.ret(returnExpression.get()));
        }
        Optional<String> label = (Optional<String>) node.child5().accept(this);
        builder.setAssociativity(associativity);
        if (label.isPresent()) {
            builder.setLabel(label.get());
        }
        return builder.build();
    }

    @Override
    public Object visitSingleElemSequence(IggyParseTree.SingleElemSequence node) {
        Sequence.Builder builder = new Sequence.Builder();
        Optional<List<Expression>> expressions = (Optional<List<Expression>>) node.child0().accept(this);
        Symbol symbol = (Symbol) node.child1().accept(this);
        SymbolBuilder<? extends Symbol> symbolBuilder = symbol.copy();
        if (expressions.isPresent()) {
            for (Expression expression : expressions.get()) {
                symbolBuilder.addPreCondition(DataDependentCondition.predicate(expression));
            }
        }
        Optional<Expression> returnExpression = (Optional<Expression>) node.child2().accept(this);
        if (returnExpression.isPresent()) {
            builder.addSymbol(Return.ret(returnExpression.get()));
        }
        builder.addSymbol(symbolBuilder.build());
        Optional<String> label = (Optional<String>) node.child3().accept(this);
        if (label.isPresent()) {
            builder.setLabel(label.get());
        }
        return builder.build();
    }

    @Override
    public Object visitCondition(IggyParseTree.Condition node) {
        return visitChildren(node);
    }

    @Override
    public Nonterminal visitCallSymbol(IggyParseTree.CallSymbol node) {
        Expression[] expressions = ((List<Expression>) node.child1().accept(this)).toArray(new Expression[]{});
        return new Nonterminal.Builder(getIdentifier(node.child0()).getName()).apply(expressions).build();
    }

    @Override
    public Offside visitOffsideSymbol(IggyParseTree.OffsideSymbol node) {
        return Offside.offside((Symbol) node.child1().accept(this));
    }

    @Override
    public Star visitStarSymbol(IggyParseTree.StarSymbol node) {
        return Star.from((Symbol) node.child0().accept(this));
    }

    @Override
    public Plus visitPlusSymbol(IggyParseTree.PlusSymbol node) {
        return Plus.from((Symbol) node.child0().accept(this));
    }

    @Override
    public Object visitOptionSymbol(IggyParseTree.OptionSymbol node) {
        return Opt.from((Symbol) node.child0().accept(this));
    }

    @Override
    public Object visitSequenceSymbol(IggyParseTree.SequenceSymbol node) {
        List<Symbol> symbols = new ArrayList<>();
        symbols.add((Symbol) node.child1().accept(this));
        symbols.addAll((List<? extends Symbol>) node.child2().accept(this));
        return Group.from(symbols);
    }

    @Override
    public Object visitAlternationSymbol(IggyParseTree.AlternationSymbol node) {
        List<Symbol> symbols = new ArrayList<>();
        List<Symbol> first = (List<Symbol>) node.child1().accept(this);
        List<List<Symbol>> second = (List<List<Symbol>>) node.child2().accept(this);
        if (first.size() == 1) {
            symbols.add(first.get(0));
        } else {
            symbols.add(Group.from(first));
        }
        for (List<Symbol> list : second) {
            if (list.size() == 1) {
                symbols.add(list.get(0));
            } else {
                symbols.add(Group.from(list));
            }
        }
        return Alt.from(symbols);
    }

    @Override
    public Object visitAlignSymbol(IggyParseTree.AlignSymbol node) {
        return Align.align((Symbol) node.childAt(1).accept(this));
    }

    @Override
    public Object visitIgnoreSymbol(IggyParseTree.IgnoreSymbol node) {
        return Ignore.ignore((Symbol) node.childAt(1).accept(this));
    }

    @Override
    public Object visitLabeledSymbol(IggyParseTree.LabeledSymbol node) {
        Symbol symbol = (Symbol) node.childAt(2).accept(this);
        return symbol.copy().setLabel(getIdentifier(node.childAt(0)).getName()).build();
    }

    @Override
    public Object visitStatementSymbol(IggyParseTree.StatementSymbol node) {
        Symbol symbol = (Symbol) node.childAt(0).accept(this);
        List<List<Statement>> statements = (List<List<Statement>>) node.childAt(1).accept(this);
        return Code.code(symbol, flatten(statements).toArray(new Statement[0]));
    }

    @Override
    public Object visitPostConditionSymbol(IggyParseTree.PostConditionSymbol node) {
        Symbol symbol = (Symbol) node.childAt(0).accept(this);
        List<Expression> expressions = (List<Expression>) node.childAt(1).accept(this);
        SymbolBuilder<? extends Symbol> builder = symbol.copy();
        for (Expression expression : expressions) {
            builder.addPostCondition(DataDependentCondition.predicate(expression));
        }
        return builder.build();
    }

    @Override
    public Object visitPrecedeSymbol(IggyParseTree.PrecedeSymbol node) {
        Symbol symbol = (Symbol) node.childAt(2).accept(this);
        RegularExpression regex = (RegularExpression) node.childAt(0).accept(this);
        return symbol.copy().addPreCondition(RegularExpressionCondition.precede(regex)).build();
    }

    @Override
    public Object visitNotPrecedeSymbol(IggyParseTree.NotPrecedeSymbol node) {
        Symbol symbol = (Symbol) node.childAt(2).accept(this);
        RegularExpression regex = (RegularExpression) node.childAt(0).accept(this);
        return symbol.copy().addPreCondition(RegularExpressionCondition.notPrecede(regex)).build();
    }

    @Override
    public Object visitFollowSymbol(IggyParseTree.FollowSymbol node) {
        Symbol symbol = (Symbol) node.childAt(0).accept(this);
        RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
        return symbol.copy().addPostCondition(RegularExpressionCondition.follow(regex)).build();
    }

    @Override
    public Object visitNotFollowSymbol(IggyParseTree.NotFollowSymbol node) {
        Symbol symbol = (Symbol) node.childAt(0).accept(this);
        RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
        return symbol.copy().addPostCondition(RegularExpressionCondition.notFollow(regex)).build();
    }

    @Override
    public Object visitExcludeSymbol(IggyParseTree.ExcludeSymbol node) {
        Symbol symbol = (Symbol) node.childAt(0).accept(this);
        RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
        return symbol.copy().addPostCondition(RegularExpressionCondition.notMatch(regex)).build();
    }

    @Override
    public Object visitExceptSymbol(IggyParseTree.ExceptSymbol node) {
        Identifier symbol = (Identifier) node.childAt(0).accept(this);
        return symbol.copy().addExcept(getIdentifier(node.childAt(2)).getName()).build();
    }

    @Override
    public Object visitIfThenElseSymbol(IggyParseTree.IfThenElseSymbol node) {
        return IfThenElse.ifThenElse(
            (Expression) node.childAt(1).accept(this),
            (Symbol) node.childAt(2).accept(this),
            (Symbol) node.childAt(4).accept(this)
        );
    }

    @Override
    public Object visitIdentifierSymbol(IggyParseTree.IdentifierSymbol node) {
        return org.iguana.grammar.symbol.Identifier.fromName(getIdentifier(node).getName());
    }

    @Override
    public Object visitStringSymbol(IggyParseTree.StringSymbol node) {
        String text = stripQuotes(node);
        RegularExpression regex = getCharsRegex(text);
        literals.put(text, regex);
        return new Terminal.Builder(regex)
            .setNodeType(TerminalNodeType.Literal)
            .build();
    }

    @Override
    public Object visitStarSepSymbol(IggyParseTree.StarSepSymbol node) {
        Symbol symbol = (Symbol) node.childAt(1).accept(this);
        List<Symbol> seps = (List<Symbol>) node.childAt(2).accept(this);
        return new Star.Builder(symbol).addSeparators(seps).build();
    }

    @Override
    public Object visitPlusSepSymbol(IggyParseTree.PlusSepSymbol node) {
        Symbol symbol = (Symbol) node.childAt(1).accept(this);
        List<Symbol> seps = (List<Symbol>) node.childAt(2).accept(this);
        return new Plus.Builder(symbol).addSeparators(seps).build();
    }

    @Override
    public Object visitArguments(IggyParseTree.Arguments node) {
        return visitChildren(node);
    }

    @Override
    public Object visitCallStatement(IggyParseTree.CallStatement node) {
        return Collections.singletonList(AST.stat(getCall(node)));
    }

    @Override
    public List<Statement> visitBindingStatement(IggyParseTree.BindingStatement node) {
        return (List<Statement>) node.childAt(0).accept(this);
    }

    @Override
    public List<Statement> visitAssignBinding(IggyParseTree.AssignBinding node) {
        List<Statement> statements = new ArrayList<>();
        statements.add(AST.stat(AST.assign(getIdentifier(node.childAt(0)).getName(), (Expression) node.childAt(2).accept(this))));
        return statements;
    }

    @Override
    public List<Statement> visitDeclareBinding(IggyParseTree.DeclareBinding node) {
        List<Statement> statements = new ArrayList<>();
        String label = node.getGrammarDefinition().getLabel();
        List<Object> elems = (List<Object>) node.childAt(1).accept(this);
        int i = 0;
        while (i < elems.size()) {
            statements.add(AST.varDeclStat(((Identifier) elems.get(i)).getName(), (Expression) elems.get(i + 1)));
            i += 2;
        }
        return statements;
    }

    @Override
    public org.iguana.regex.Star visitStarRegex(IggyParseTree.StarRegex node) {
        return org.iguana.regex.Star.from((RegularExpression) node.childAt(0).accept(this));
    }

    @Override
    public org.iguana.regex.Plus visitPlusRegex(IggyParseTree.PlusRegex node) {
        return org.iguana.regex.Plus.from((RegularExpression) node.childAt(0).accept(this));
    }

    @Override
    public org.iguana.regex.Opt visitOptionRegex(IggyParseTree.OptionRegex node) {
        return org.iguana.regex.Opt.from((RegularExpression) node.childAt(0).accept(this));
    }

    @Override
    public RegularExpression visitBracketRegex(IggyParseTree.BracketRegex node) {
        return (RegularExpression) node.child1().accept(this);
    }

    @Override
    public org.iguana.regex.Seq visitSequenceRegex(IggyParseTree.SequenceRegex node) {
        List<RegularExpression> list = new ArrayList<>();
        list.add((RegularExpression) node.childAt(1).accept(this));
        list.addAll((Collection<? extends RegularExpression>) node.childAt(2).accept(this));
        return org.iguana.regex.Seq.from(list);
    }

    @Override
    public org.iguana.regex.Alt<?> visitAlternationRegex(IggyParseTree.AlternationRegex node) {
        List<List<RegularExpression>> listOfList = (List<List<RegularExpression>>) node.childAt(1).accept(this);
        List<RegularExpression> list = listOfList.stream().map(l -> {
            if (l.size() == 1) return l.get(0);
            else return Seq.from(l);
        }).collect(Collectors.toList());
        return org.iguana.regex.Alt.from(list);
    }

    @Override
    public org.iguana.regex.Reference visitNontRegex(IggyParseTree.NontRegex node) {
        return org.iguana.regex.Reference.from(getIdentifier(node.childAt(0)).getName());
    }

    @Override
    public org.iguana.regex.Alt<RegularExpression> visitCharClassRegex(IggyParseTree.CharClassRegex node) {
        return (org.iguana.regex.Alt<RegularExpression>) node.childAt(0).accept(this);
    }

    @Override
    public Object visitStringRegex(IggyParseTree.StringRegex node) {
        return getCharsRegex(stripQuotes(node));
    }

    @Override
    public Object visitCharsCharClass(IggyParseTree.CharsCharClass node) {
        return org.iguana.regex.Alt.from((List<RegularExpression>) node.childAt(1).accept(this));
    }

    @Override
    public Object visitNotCharsCharClass(IggyParseTree.NotCharsCharClass node) {
        return org.iguana.regex.Alt.not((List<RegularExpression>) node.childAt(2).accept(this));
    }

    @Override
    public CharRange visitRangeRange(IggyParseTree.RangeRange node) {
        int start = getRangeChar(node.childAt(0).getText());
        int end = getRangeChar(node.childAt(2).getText());
        return CharRange.in(start, end);
    }

    @Override
    public Char visitCharacterRange(IggyParseTree.CharacterRange node) {
        int c = getRangeChar(node.childAt(0).getText());
        return Char.from(c);
    }

    @Override
    public Expression.Call visitCallExpression(IggyParseTree.CallExpression node) {
        return getCall(node);
    }

    @Override
    public Expression.Not visitNotExpression(IggyParseTree.NotExpression node) {
        Expression exp = (Expression) node.childAt(1).accept(this);
        return AST.not(exp);
    }

    @Override
    public Expression.Multiply visitMultiplicationExpression(IggyParseTree.MultiplicationExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.multiply(lhs, rhs);
    }

    @Override
    public Expression.Divide visitDivisionExpression(IggyParseTree.DivisionExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.divide(lhs, rhs);
    }

    @Override
    public Expression.Add visitAdditionExpression(IggyParseTree.AdditionExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.add(lhs, rhs);
    }

    @Override
    public Expression.Subtract visitSubtractionExpression(IggyParseTree.SubtractionExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.subtract(lhs, rhs);
    }

    @Override
    public Object visitGreaterEqExpression(IggyParseTree.GreaterEqExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.greaterEq(lhs, rhs);
    }

    @Override
    public Expression.LessThanEqual visitLessEqExpression(IggyParseTree.LessEqExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.lessEq(lhs, rhs);
    }

    @Override
    public Expression.Greater visitGreaterExpression(IggyParseTree.GreaterExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.greater(lhs, rhs);
    }

    @Override
    public Expression.Less visitLessExpression(IggyParseTree.LessExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.less(lhs, rhs);
    }

    @Override
    public Expression.Equal visitEqualExpression(IggyParseTree.EqualExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.equal(lhs, rhs);
    }

    @Override
    public Expression.NotEqual visitNotEqualExpression(IggyParseTree.NotEqualExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.notEqual(lhs, rhs);
    }

    @Override
    public Expression.And visitAndExpression(IggyParseTree.AndExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.and(lhs, rhs);
    }

    @Override
    public Expression.Or visitOrExpression(IggyParseTree.OrExpression node) {
        Expression lhs = (Expression) node.childAt(0).accept(this);
        Expression rhs = (Expression) node.childAt(2).accept(this);
        return AST.or(lhs, rhs);
    }

    @Override
    public Expression.LeftExtent visitLExtentExpression(IggyParseTree.LExtentExpression node) {
        String l = node.childAt(0).getText();
        return AST.lExt(l);
    }

    @Override
    public Expression.RightExtent visitRExtentExpression(IggyParseTree.RExtentExpression node) {
        String r = node.childAt(0).getText();
        return AST.rExt(r);
    }

    @Override
    public Expression.Yield visitYieldExpression(IggyParseTree.YieldExpression node) {
        String yield = node.childAt(0).getText();
        return AST.yield(yield);
    }

    @Override
    public Expression.Val visitValExpression(IggyParseTree.ValExpression node) {
        String val = node.childAt(0).getText();
        return AST.val(val);
    }

    @Override
    public Expression.Name visitNameExpression(IggyParseTree.NameExpression node) {
        return AST.var((node.childAt(0).getText()));
    }

    @Override
    public Expression.Integer visitNumberExpression(IggyParseTree.NumberExpression node) {
        return AST.integer(Integer.parseInt(node.childAt(0).getText()));
    }

    @Override
    public Expression visitBracketExpression(IggyParseTree.BracketExpression node) {
        return (Expression) node.childAt(1).accept(this);
    }

    @Override
    public Expression visitReturnExpression(IggyParseTree.ReturnExpression node) {
        return (Expression) node.childAt(1).accept(this);
    }

    @Override
    public Identifier visitVarName(IggyParseTree.VarName node) {
        return getIdentifier(node);
    }

    @Override
    public String visitLabel(IggyParseTree.Label node) {
        return getIdentifier(node.childAt(1)).getName();
    }

    @Override
    public Identifier visitName(IggyParseTree.Name node) {
        return getIdentifier(node);
    }

    @Override
    public Identifier visitIdentifier(IggyParseTree.Identifier node) {
        return getIdentifier(node);
    }

    private static Identifier getIdentifier(ParseTreeNode node) {
        return Identifier.fromName(node.getText());
    }

    private static RegularExpression getRegex(List<List<RegularExpression>> listOfList) {
        if (listOfList.size() == 1) {
            return getRegexOfList(listOfList.get(0));
        }
        org.iguana.regex.Alt.Builder<RegularExpression> builder = new org.iguana.regex.Alt.Builder<>();
        for (List<RegularExpression> list : listOfList) {
            builder.add(getRegexOfList(list));
        }
        return builder.build();
    }

    private static RegularExpression getRegexOfList(List<RegularExpression> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        return org.iguana.regex.Seq.from(list);
    }

    private static Associativity getAssociativity(ParseTreeNode node) {
        if (node == null) return null;
        switch (node.getText()) {
            case "left":
                return Associativity.LEFT;
            case "right":
                return Associativity.RIGHT;
            case "non-assoc":
                return Associativity.NON_ASSOC;
            default:
                return Associativity.UNDEFINED;
        }
    }

    private Expression.Call getCall(NonterminalNode node) {
        String funName = node.childAt(0).getText();
        Expression[] arguments = ((List<Expression>) node.childAt(1).accept(this)).toArray(new Expression[]{});
        switch (funName) {
            case "println":
                return AST.println(arguments);
            case "indent":
                return AST.indent(arguments[0]);
            case "assert":
                return AST.assertion(arguments);
            case "set":
                return AST.set(arguments);
            case "put":
                return AST.put(arguments[0], arguments[1]);
            case "contains":
                return AST.contains(arguments[0], arguments[1]);
            default:
                throw new RuntimeException("Unknown function name: " + funName);
        }
    }

    private static String stripQuotes(NonterminalNode node) {
        return node.getText().substring(1, node.getText().length() - 1);
    }

    private static int[] getChars(String s) {
        int i = 0;
        int j = 0;
        int[] chars = new int[s.length()];
        while (i < s.length()) {
            if (s.charAt(i) == '\\') {
                switch (s.charAt(i + 1)) {
                    case  'n': chars[j++] = '\n'; break;
                    case  'r': chars[j++] = '\r'; break;
                    case  't': chars[j++] = '\t'; break;
                    case  'f': chars[j++] = '\f'; break;
                    case  ' ': chars[j++] = ' ';  break;
                    case '\\': chars[j++] = '\\'; break;
                    case '\'': chars[j++] = '\''; break;
                    case  '"': chars[j++] = '"';  break;
                }
                i += 2;
            } else {
                chars[j++] = s.charAt(i++);
            }
        }
        return Arrays.copyOf(chars, j);
    }

    private static RegularExpression getCharsRegex(String s) {
        int[] chars = getChars(s);
        if (chars.length == 1) {
            return Char.from(chars[0]);
        }
        return org.iguana.regex.Seq.from(chars);
    }

    private static int getRangeChar(String s) {
        switch (s) {
            case "\\n":
                return '\n';
            case "\\r":
                return '\r';
            case "\\t":
                return '\t';
            case "\\f":
                return '\f';
            case "\\'":
                return '\'';
            case "\\\"":
                return '\"';
            case "\\ ":
                return ' ';
            case "\\[":
                return '[';
            case "\\]":
                return ']';
            case "\\-":
                return '-';
        }
        return s.charAt(0);
    }
}
