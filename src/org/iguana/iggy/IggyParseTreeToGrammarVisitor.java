package org.iguana.iggy;

import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.PositionalCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.Error;
import org.iguana.grammar.symbol.*;
import org.iguana.iggy.gen.IggyParseTree;
import org.iguana.iggy.gen.IggyParseTree.RegexRule;
import org.iguana.iggy.gen.IggyParseTreeVisitor;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.regex.Char;
import org.iguana.regex.CharRange;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Seq;
import org.iguana.util.Tuple;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.iguana.utils.collections.CollectionsUtil.flatten;

public class IggyParseTreeToGrammarVisitor implements IggyParseTreeVisitor<Object> {

    // A map from the name of the regular expressions (as they are defined in the grammar) to the regular expression.
    private final Map<String, RegularExpression> regularExpressionDefinitions = new LinkedHashMap<>();

    private final Map<String, RegularExpression> literals = new LinkedHashMap<>();

    private final List<String> startSymbols = new ArrayList<>();
    private Identifier layout;

    @Override
    public Grammar visitGrammar(IggyParseTree.Grammar node) {
        Optional<Identifier> name = visit(node.name());
        Grammar.Builder builder = new Grammar.Builder();
        List<Object> defs = visit(node.defs());

        final Map<String, Expression> globals = new HashMap<>();

        for (Object def : defs) {
            if (def instanceof Rule) {
                builder.addRule((Rule) def);
            } else { // Tuple<String, Expression)
                Tuple<String, Expression> var = (Tuple<String, Expression>) def;
                globals.put(var.getFirst(), var.getSecond());
            }
        }
        for (Map.Entry<String, RegularExpression> entry : regularExpressionDefinitions.entrySet()) {
            builder.addRegularExpression(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Expression> entry : globals.entrySet()) {
            builder.addGlobal(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, RegularExpression> entry : literals.entrySet()) {
            builder.addLiteral(entry.getKey(), entry.getValue());
        }
        for (String start : startSymbols) {
            builder.addStartSymbol(Start.from(start));
        }
        builder.setLayout(layout);
        name.ifPresent(identifier -> builder.setName(identifier.getName()));
        return builder.build();
    }

    @Override
    public Tuple<String, Expression> visitTopLevelVar(IggyParseTree.TopLevelVar node) {
        String key = node.id().getText();
        Expression value = (Expression) node.exp().accept(this);
        return Tuple.of(key, value);
    }

    @Override
    public Rule visitContextFreeRule(IggyParseTree.ContextFreeRule node) {
        Identifier nonterminalName = (Identifier) node.name().accept(this);
        Optional<List<Identifier>> parameters = visit(node.params());
        List<PriorityLevel> priorityLevels = visit(node.body());

        LayoutStrategy layoutStrategy = LayoutStrategy.INHERITED;
        if (node.modifier().hasChildren()) {
            String text = node.modifier().getText();
            if (text.equals("start")) {
                startSymbols.add(nonterminalName.getName());
            } else if (text.equals("lexical")) {
                layoutStrategy = LayoutStrategy.NO_LAYOUT;
            } else { // "layout"
                layout = nonterminalName;
                layoutStrategy = LayoutStrategy.NO_LAYOUT;
            }
        }

        NonterminalNodeType nonterminalNodeType;
        if (layout == null) {
            nonterminalNodeType = NonterminalNodeType.Basic;
        } else {
            if (layout.getName().equals(nonterminalName.getName())) {
                nonterminalNodeType = NonterminalNodeType.Layout;
            } else {
                nonterminalNodeType = NonterminalNodeType.Basic;
            }
        }
        Nonterminal nonterminal = new Nonterminal.Builder(nonterminalName.getName())
            .addParameters(parameters.map(identifiers -> identifiers.stream().map(AbstractSymbol::toString)
                .collect(Collectors.toList())).orElse(Collections.emptyList()))
            .setNodeType(nonterminalNodeType)
            .build();

        return new Rule.Builder(nonterminal)
            .addPriorityLevels(priorityLevels)
            .setLayoutStrategy(layoutStrategy)
            .build();
    }

    @Override
    public Void visitRegexRule(RegexRule node) {
        List<List<RegularExpression>> alts = visit(node.body());
        Identifier identifier = (Identifier) node.name().accept(this);
        regularExpressionDefinitions.put(identifier.getName(), getRegex(alts));

        if (node.modifier().hasChildren()) {
            layout = identifier;
        }

        return null;
    }

    @Override
    public List<Identifier> visitParameters(IggyParseTree.Parameters node) {
        return visitChildren(node);
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
        builder.addAlternatives(visitChildren(node));
        return builder.build();
    }

    @Override
    public Alternative visitSequenceAlternative(IggyParseTree.SequenceAlternative node) {
        Alternative.Builder builder = new Alternative.Builder();
        builder.addSequence((Sequence) node.seq().accept(this));
        return builder.build();
    }

    @Override
    public Alternative visitAssociativityAlternative(IggyParseTree.AssociativityAlternative node) {
        Associativity associativity = getAssociativity(node.assoc());
        List<Sequence> seqs = visit(node.seqs());
        return new Alternative.Builder(seqs, associativity).build();
    }

    @Override
    public Sequence visitMoreThanOneElemSequence(IggyParseTree.MoreThanOneElemSequence node) {
        Sequence.Builder builder = new Sequence.Builder();

        if (node.assoc().hasChildren()) {
            builder.setAssociativity(getAssociativity(node.assoc()));
        }

        Optional<List<Expression>> expressions = visit(node.cond());
        List<Symbol> symbols = new ArrayList<>();
        Symbol symbol = (Symbol) node.first().accept(this);
        SymbolBuilder<? extends Symbol> symbolBuilder = symbol.copy();
        if (expressions.isPresent()) {
            for (Expression expression : expressions.get()) {
                symbolBuilder.addPreCondition(DataDependentCondition.predicate(expression));
            }
        }
        symbols.add(symbolBuilder.build());
        symbols.addAll(visit(node.rest()));
        builder.addSymbols(symbols);
        Optional<Expression> returnExpression = visit(node.ret());
        if (returnExpression.isPresent()) {
            builder.addSymbol(Return.ret(returnExpression.get()));
        }
        Optional<String> label = visit(node.label());
        if (label.isPresent()) {
            builder.setLabel(label.get());
        }
        return builder.build();
    }

    @Override
    public Sequence visitSingleElemSequence(IggyParseTree.SingleElemSequence node) {
        Sequence.Builder builder = new Sequence.Builder();
        Optional<List<Expression>> expressions = visit(node.cond());
        Symbol symbol = (Symbol) node.sym().accept(this);
        SymbolBuilder<? extends Symbol> symbolBuilder = symbol.copy();
        if (expressions.isPresent()) {
            for (Expression expression : expressions.get()) {
                symbolBuilder.addPreCondition(DataDependentCondition.predicate(expression));
            }
        }
        Optional<Expression> returnExpression = visit(node.ret());
        if (returnExpression.isPresent()) {
            builder.addSymbol(Return.ret(returnExpression.get()));
        }
        builder.addSymbol(symbolBuilder.build());
        Optional<String> label = visit(node.label());
        if (label.isPresent()) {
            builder.setLabel(label.get());
        }
        return builder.build();
    }

    @Override
    public Sequence visitEmptySequence(IggyParseTree.EmptySequence node) {
        Optional<String> label = visit(node.label());
        return new Sequence.Builder().setLabel(label.orElse(null)).build();
    }

    @Override
    public Object visitCondition(IggyParseTree.Condition node) {
        return visitChildren(node);
    }

    @Override
    public Nonterminal visitCallSymbol(IggyParseTree.CallSymbol node) {
        Expression[] expressions = ((List<Expression>) node.args().accept(this)).toArray(new Expression[]{});
        Identifier id = (Identifier) node.id().accept(this);
        return new Nonterminal.Builder(id.getName()).apply(expressions).build();
    }

    @Override
    public Offside visitOffsideSymbol(IggyParseTree.OffsideSymbol node) {
        return Offside.offside((Symbol) node.sym().accept(this));
    }

    @Override
    public Star visitStarSymbol(IggyParseTree.StarSymbol node) {
        return Star.from((Symbol) node.sym().accept(this));
    }

    @Override
    public Plus visitPlusSymbol(IggyParseTree.PlusSymbol node) {
        return Plus.from((Symbol) node.sym().accept(this));
    }

    @Override
    public Opt visitOptionSymbol(IggyParseTree.OptionSymbol node) {
        return Opt.from((Symbol) node.sym().accept(this));
    }

    @Override
    public Group visitSequenceSymbol(IggyParseTree.SequenceSymbol node) {
        List<Symbol> symbols = visit(node.syms());
        return Group.from(symbols);
    }

    @Override
    public Alt visitAlternationSymbol(IggyParseTree.AlternationSymbol node) {
        List<Symbol> symbols = new ArrayList<>();
        List<Symbol> first = visit(node.first());
        List<List<Symbol>> second = visit(node.rest());
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
    public Align visitAlignSymbol(IggyParseTree.AlignSymbol node) {
        return Align.align((Symbol) node.sym().accept(this));
    }

    @Override
    public Ignore visitIgnoreSymbol(IggyParseTree.IgnoreSymbol node) {
        return Ignore.ignore((Symbol) node.sym().accept(this));
    }

    @Override
    public Symbol visitLabeledSymbol(IggyParseTree.LabeledSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        Identifier id = (Identifier) node.id().accept(this);
        return symbol.copy().setLabel(id.getName()).build();
    }

    @Override
    public Symbol visitStatementSymbol(IggyParseTree.StatementSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        List<List<Statement>> statements = visit(node.stmts());
        return Code.code(symbol, flatten(statements).toArray(new Statement[0]));
    }

    @Override
    public Symbol visitPostConditionSymbol(IggyParseTree.PostConditionSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        List<Expression> expressions = visit(node.cond());
        SymbolBuilder<? extends Symbol> builder = symbol.copy();
        for (Expression expression : expressions) {
            builder.addPostCondition(DataDependentCondition.predicate(expression));
        }
        return builder.build();
    }

    @Override
    public Symbol visitPrecedeSymbol(IggyParseTree.PrecedeSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        RegularExpression regex = (RegularExpression) node.reg().accept(this);
        return symbol.copy().addPreCondition(RegularExpressionCondition.precede(regex)).build();
    }

    @Override
    public Symbol visitNotPrecedeSymbol(IggyParseTree.NotPrecedeSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        RegularExpression regex = (RegularExpression) node.reg().accept(this);
        return symbol.copy().addPreCondition(RegularExpressionCondition.notPrecede(regex)).build();
    }

    @Override
    public Symbol visitStartOfLineSymbol(IggyParseTree.StartOfLineSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        return symbol.copy().addPreCondition(PositionalCondition.startOfLineCondition()).build();
    }

    @Override
    public Symbol visitFollowSymbol(IggyParseTree.FollowSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        RegularExpression regex = (RegularExpression) node.reg().accept(this);
        return symbol.copy().addPostCondition(RegularExpressionCondition.follow(regex)).build();
    }

    @Override
    public Symbol visitNotFollowSymbol(IggyParseTree.NotFollowSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        RegularExpression regex = (RegularExpression) node.reg().accept(this);
        return symbol.copy().addPostCondition(RegularExpressionCondition.notFollow(regex)).build();
    }

    @Override
    public Symbol visitExcludeSymbol(IggyParseTree.ExcludeSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        RegularExpression regex = (RegularExpression) node.reg().accept(this);
        return symbol.copy().addPostCondition(RegularExpressionCondition.notMatch(regex)).build();
    }

    @Override
    public Symbol visitExceptSymbol(IggyParseTree.ExceptSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        Identifier id = (Identifier) node.id().accept(this);
        if (symbol instanceof Identifier) {
            return ((Identifier) symbol).copy().addExcept(id.getName()).build();
        } else {
            // TODO: I think nonterminal calls are also allowed here, handle it.
            throw new RuntimeException("Unexpected symbol");
        }
    }

    @Override
    public Symbol visitEndOfLineSymbol(IggyParseTree.EndOfLineSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        return symbol.copy().addPreCondition(PositionalCondition.endOfLineCondition()).build();
    }

    @Override
    public Symbol visitEndOfFileSymbol(IggyParseTree.EndOfFileSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        return symbol.copy().addPreCondition(PositionalCondition.endOfFileCondition()).build();
    }

    @Override
    public Symbol visitIfThenElseSymbol(IggyParseTree.IfThenElseSymbol node) {
        return IfThenElse.ifThenElse(
            (Expression) node.exp().accept(this),
            (Symbol) node.thenPart().accept(this),
            (Symbol) node.elsePart().accept(this)
        );
    }

    @Override
    public Identifier visitIdentifierSymbol(IggyParseTree.IdentifierSymbol node) {
        return (Identifier) node.id().accept(this);
    }

    @Override
    public Terminal visitStringSymbol(IggyParseTree.StringSymbol node) {
        String text = stripQuotes(node);
        RegularExpression regex = getCharsRegex(text);
        literals.put(text, regex);
        return new Terminal.Builder(regex)
            .setNodeType(TerminalNodeType.Literal)
            .build();
    }

    @Override
    public Terminal visitCharClassSymbol(IggyParseTree.CharClassSymbol node) {
        RegularExpression regex = (RegularExpression) node.charClass().accept(this);
        return new Terminal.Builder(regex)
            .setNodeType(TerminalNodeType.Regex)
            .build();
    }

    @Override
    public Star visitStarSepSymbol(IggyParseTree.StarSepSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        List<Symbol> seps = visit(node.sep());
        return new Star.Builder(symbol).addSeparators(seps).build();
    }

    @Override
    public Plus visitPlusSepSymbol(IggyParseTree.PlusSepSymbol node) {
        Symbol symbol = (Symbol) node.sym().accept(this);
        List<Symbol> seps = visit(node.sep());
        return new Plus.Builder(symbol).addSeparators(seps).build();
    }

    @Override
    public Object visitErrorSymbol(IggyParseTree.ErrorSymbol node) {
        return Error.getInstance();
    }

    @Override
    public List<Expression> visitArguments(IggyParseTree.Arguments node) {
        return visitChildren(node);
    }

    @Override
    public Object visitCallStatement(IggyParseTree.CallStatement node) {
        Function<Expression[], Expression.Call> fun = (Function<Expression[], Expression.Call>) node.fun().accept(this);
        Expression[] arguments = ((List<Expression>) node.args().accept(this)).toArray(new Expression[]{});
        return Collections.singletonList(AST.stat(fun.apply(arguments)));
    }

    @Override
    public List<Statement> visitBindingStatement(IggyParseTree.BindingStatement node) {
        return visit(node.bindings());
    }

    @Override
    public List<Statement> visitAssignBinding(IggyParseTree.AssignBinding node) {
        List<Statement> statements = new ArrayList<>();
        Identifier id = (Identifier) node.varName().accept(this);
        statements.add(AST.stat(AST.assign(id.getName(), (Expression) node.exp().accept(this))));
        return statements;
    }

    @Override
    public List<Statement> visitDeclareBinding(IggyParseTree.DeclareBinding node) {
        List<Statement> statements = new ArrayList<>();
        List<Object> elems = visit(node.decls());
        int i = 0;
        while (i < elems.size()) {
            statements.add(AST.varDeclStat(((Identifier) elems.get(i)).getName(), (Expression) elems.get(i + 1)));
            i += 2;
        }
        return statements;
    }

    @Override
    public org.iguana.regex.Star visitStarRegex(IggyParseTree.StarRegex node) {
        return org.iguana.regex.Star.from((RegularExpression) node.reg().accept(this));
    }

    @Override
    public org.iguana.regex.Plus visitPlusRegex(IggyParseTree.PlusRegex node) {
        return org.iguana.regex.Plus.from((RegularExpression) node.reg().accept(this));
    }

    @Override
    public org.iguana.regex.Opt visitOptionRegex(IggyParseTree.OptionRegex node) {
        return org.iguana.regex.Opt.from((RegularExpression) node.reg().accept(this));
    }

    @Override
    public RegularExpression visitBracketRegex(IggyParseTree.BracketRegex node) {
        return (RegularExpression) node.reg().accept(this);
    }

    @Override
    public org.iguana.regex.Seq<?> visitSequenceRegex(IggyParseTree.SequenceRegex node) {
        List<RegularExpression> list = new ArrayList<>();
        list.add((RegularExpression) node.first().accept(this));
        list.addAll(visit(node.rest()));
        return org.iguana.regex.Seq.from(list);
    }

    @Override
    public org.iguana.regex.Alt<?> visitAlternationRegex(IggyParseTree.AlternationRegex node) {
        List<RegularExpression> first = visit(node.first());
        List<List<RegularExpression>> rest = visit(node.rest());
        List<RegularExpression> res = new ArrayList<>(first);
        res.addAll(rest.stream().map(l -> {
            if (l.size() == 1) return l.get(0);
            else return Seq.from(l);
        }).collect(Collectors.toList()));
        return org.iguana.regex.Alt.from(res);
    }

    @Override
    public org.iguana.regex.Reference visitNontRegex(IggyParseTree.NontRegex node) {
        Identifier id = (Identifier) node.name().accept(this);
        return org.iguana.regex.Reference.from(id.getName());
    }

    @Override
    public org.iguana.regex.Alt<RegularExpression> visitCharClassRegex(IggyParseTree.CharClassRegex node) {
        return visit(node.charClass());
    }

    @Override
    public RegularExpression visitStringRegex(IggyParseTree.StringRegex node) {
        return getCharsRegex(stripQuotes(node));
    }

    @Override
    public org.iguana.regex.Alt<?> visitCharsCharClass(IggyParseTree.CharsCharClass node) {
        return org.iguana.regex.Alt.from((List<RegularExpression>) node.ranges().accept(this));
    }

    @Override
    public org.iguana.regex.Alt<?> visitNotCharsCharClass(IggyParseTree.NotCharsCharClass node) {
        return org.iguana.regex.Alt.not((List<RegularExpression>) node.ranges().accept(this));
    }

    @Override
    public CharRange visitRangeRange(IggyParseTree.RangeRange node) {
        int start = getRangeChar(node.first().getText());
        int end = getRangeChar(node.second().getText());
        return CharRange.in(start, end);
    }

    @Override
    public Char visitCharacterRange(IggyParseTree.CharacterRange node) {
        int c = getRangeChar(node.range().getText());
        return Char.from(c);
    }

    @Override
    public Expression.Call visitCallExpression(IggyParseTree.CallExpression node) {
        Function<Expression[], Expression.Call> fun = (Function<Expression[], Expression.Call>) node.fun().accept(this);
        Expression[] arguments = ((List<Expression>) node.args().accept(this)).toArray(new Expression[]{});
        return fun.apply(arguments);
    }

    @Override
    public Expression.Not visitNotExpression(IggyParseTree.NotExpression node) {
        Expression exp = visit(node.exp());
        return AST.not(exp);
    }

    @Override
    public Expression.Multiply visitMultiplicationExpression(IggyParseTree.MultiplicationExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.multiply(lhs, rhs);
    }

    @Override
    public Expression.Divide visitDivisionExpression(IggyParseTree.DivisionExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.divide(lhs, rhs);
    }

    @Override
    public Expression.Add visitAdditionExpression(IggyParseTree.AdditionExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.add(lhs, rhs);
    }

    @Override
    public Expression.Subtract visitSubtractionExpression(IggyParseTree.SubtractionExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.subtract(lhs, rhs);
    }

    @Override
    public Expression.GreaterThanEqual visitGreaterEqExpression(IggyParseTree.GreaterEqExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.greaterEq(lhs, rhs);
    }

    @Override
    public Expression.LessThanEqual visitLessEqExpression(IggyParseTree.LessEqExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.lessEq(lhs, rhs);
    }

    @Override
    public Expression.Greater visitGreaterExpression(IggyParseTree.GreaterExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.greater(lhs, rhs);
    }

    @Override
    public Expression.Less visitLessExpression(IggyParseTree.LessExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.less(lhs, rhs);
    }

    @Override
    public Expression.Equal visitEqualExpression(IggyParseTree.EqualExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.equal(lhs, rhs);
    }

    @Override
    public Expression.NotEqual visitNotEqualExpression(IggyParseTree.NotEqualExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.notEqual(lhs, rhs);
    }

    @Override
    public Expression.And visitAndExpression(IggyParseTree.AndExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.and(lhs, rhs);
    }

    @Override
    public Expression.Or visitOrExpression(IggyParseTree.OrExpression node) {
        Expression lhs = visit(node.lhs());
        Expression rhs = visit(node.rhs());
        return AST.or(lhs, rhs);
    }

    @Override
    public Expression.LeftExtent visitLExtentExpression(IggyParseTree.LExtentExpression node) {
        String l = node.id().getText();
        return AST.lExt(l);
    }

    @Override
    public Expression.RightExtent visitRExtentExpression(IggyParseTree.RExtentExpression node) {
        String r = node.id().getText();
        return AST.rExt(r);
    }

    @Override
    public Expression.Yield visitYieldExpression(IggyParseTree.YieldExpression node) {
        String yield = node.id().getText();
        return AST.yield(yield);
    }

    @Override
    public Expression.Val visitValExpression(IggyParseTree.ValExpression node) {
        String val = node.id().getText();
        return AST.val(val);
    }

    @Override
    public Expression.Name visitNameExpression(IggyParseTree.NameExpression node) {
        return AST.var((node.varName().getText()));
    }

    @Override
    public Expression.Integer visitNumberExpression(IggyParseTree.NumberExpression node) {
        return AST.integer(Integer.parseInt(node.number().getText()));
    }

    @Override
    public Expression visitBracketExpression(IggyParseTree.BracketExpression node) {
        return (Expression) node.exp().accept(this);
    }

    @Override
    public Expression visitReturnExpression(IggyParseTree.ReturnExpression node) {
        return (Expression) node.exp().accept(this);
    }

    @Override
    public Identifier visitVarName(IggyParseTree.VarName node) {
        return (Identifier) node.id().accept(this);
    }

    @Override
    public String visitLabel(IggyParseTree.Label node) {
        Identifier id = (Identifier) node.id().accept(this);
        return id.getName();
    }

    @Override
    public Object visitLayout(IggyParseTree.Layout node) {
        throw new RuntimeException("Layout is handled in ContextFreeRule, this method should not be called");
    }

    @Override
    public Identifier visitName(IggyParseTree.Name node) {
        return (Identifier) node.id().accept(this);
    }

    @Override
    public Function<Expression[], Expression.Call> visitPrintlnFunName(IggyParseTree.PrintlnFunName node) {
        return AST::println;
    }

    @Override
    public Function<Expression[], Expression.Call> visitIndentFunName(IggyParseTree.IndentFunName node) {
        return AST::indent;
    }

    @Override
    public Function<Expression[], Expression.Call> visitAssertFunName(IggyParseTree.AssertFunName node) {
        return AST::assertion;
    }

    @Override
    public Function<Expression[], Expression.Call> visitSetFunName(IggyParseTree.SetFunName node) {
        return AST::set;
    }

    @Override
    public Function<Expression[], Expression.Call> visitContainsFunName(IggyParseTree.ContainsFunName node) {
        return AST::contains;
    }

    @Override
    public Function<Expression[], Expression.Call> visitPutFunName(IggyParseTree.PutFunName node) {
        return AST::put;
    }

    @Override
    public Identifier visitIdentifier(IggyParseTree.Identifier node) {
        return Identifier.fromName(node.getText());
    }

    private <T> T visit(ParseTreeNode node) {
        return (T) node.accept(this);
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

    private static String stripQuotes(ParseTreeNode node) {
        return node.getText().substring(1, node.getText().length() - 1);
    }

    private static int[] getChars(String s) {
        int i = 0;
        int j = 0;
        int[] chars = new int[s.length()];
        while (i < s.length()) {
            if (s.charAt(i) == '\\') {
                switch (s.charAt(i + 1)) {
                    case 'n':
                        chars[j++] = '\n';
                        break;
                    case 'r':
                        chars[j++] = '\r';
                        break;
                    case 't':
                        chars[j++] = '\t';
                        break;
                    case 'f':
                        chars[j++] = '\f';
                        break;
                    case ' ':
                        chars[j++] = ' ';
                        break;
                    case '\\':
                        chars[j++] = '\\';
                        break;
                    case '\'':
                        chars[j++] = '\'';
                        break;
                    case '"':
                        chars[j++] = '"';
                        break;
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
        return Seq.from(chars);
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
