package org.iguana.grammar.transformation;

import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.AbstractAST;
import org.iguana.datadependent.ast.Expression.*;
import org.iguana.datadependent.ast.Expression.Boolean;
import org.iguana.datadependent.ast.Expression.Integer;
import org.iguana.datadependent.ast.Expression.String;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.ast.Statement.Expression;
import org.iguana.datadependent.ast.VariableDeclaration;
import org.iguana.datadependent.traversal.IAbstractASTVisitor;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.PositionalCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.exception.UndeclaredVariableException;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Error;
import org.iguana.grammar.symbol.IfThenElse;
import org.iguana.traversal.IConditionVisitor;
import org.iguana.traversal.ISymbolVisitor;

import java.util.*;

public class VarToInt implements GrammarTransformation, IAbstractASTVisitor<AbstractAST>, ISymbolVisitor<Symbol>,
    IConditionVisitor<Condition> {

    private Map<java.lang.Integer, Map<java.lang.String, java.lang.Integer>> mapping;

    private Map<java.lang.String, java.lang.Integer> current;

    public Map<java.lang.Integer, Map<java.lang.String, java.lang.Integer>> getMapping() {
        return mapping;
    }

    @Override
    public RuntimeGrammar transform(RuntimeGrammar grammar) {
        mapping = new HashMap<>();
        Set<RuntimeRule> rules = new LinkedHashSet<>();
        int i = 0;
        for (RuntimeRule rule : grammar.getRules()) {
            rules.add(transform(rule));
            mapping.put(i, current);
            i++;
        }
        return RuntimeGrammar.builder()
                .addRules(rules)
                .addEBNFl(grammar.getEBNFLefts())
                .addEBNFr(grammar.getEBNFRights())
                .setLayout(grammar.getLayout())
                .build();
    }

    public RuntimeRule transform(RuntimeRule rule) {
        current = new HashMap<>();

        List<java.lang.String> parameters = rule.getHead().getParameters();

        if (parameters != null) {
            for (java.lang.String parameter : parameters)
                current.put(parameter, current.size());
        }

        List<Symbol> body = new ArrayList<>();

        for (Symbol symbol : rule.getBody())
            body.add(visit(symbol));

        return RuntimeRule.withHead(rule.getHead())
                .addSymbols(body)
                .setRecursion(rule.getRecursion())
                .setAssociativity(rule.getAssociativity())
                .setPrecedence(rule.getPrecedence())
                .setPrecedenceLevel(rule.getPrecedenceLevel())
                .setiRecursion(rule.getIRecursion())
                .setLeftEnd(rule.getLeftEnd())
                .setRightEnd(rule.getRightEnd())
                .setLeftEnds(rule.getLeftEnds())
                .setRightEnds(rule.getRightEnds())
                .setLayout(rule.getLayout())
                .setLayoutStrategy(rule.getLayoutStrategy())
                .setLabel(rule.getLabel())
                .setDefinition(rule.getDefinition())
                .build();
    }

    private Symbol visit(Symbol symbol) {
        java.lang.String label = symbol.getLabel();

        if (label != null && !label.isEmpty())
            current.put(label, current.size());

        Symbol sym = symbol.accept(this);

        Set<Condition> preConditions = new LinkedHashSet<>();
        Set<Condition> postConditions = new LinkedHashSet<>();

        for (Condition condition : symbol.getPreConditions())
            preConditions.add(condition.accept(this));

        for (Condition condition : symbol.getPostConditions())
            postConditions.add(condition.accept(this));

        return sym.copy()
                .setLabel(symbol.getLabel())
                .addPreConditions(preConditions)
                .addPostConditions(postConditions)
                .build();
    }

    @Override
    public Symbol visit(Align symbol) {
        return Align.align(visit(symbol.getSymbol()));
    }

    @Override
    public Symbol visit(Block symbol) {
        throw new RuntimeException("Unsupported symbol: var-to-int!");
    }


    @Override
    public Symbol visit(Code symbol) {
        Symbol sym = visit(symbol.getSymbol());

        Statement[] statements = new Statement[symbol.getStatements().length];
        int i = 0;
        for (Statement statement : symbol.getStatements())
            statements[i++] = (Statement) statement.accept(this);

        return Code.code(sym, statements);
    }

    @Override
    public Symbol visit(Error error) {
        return error;
    }

    @Override
    public Symbol visit(Conditional symbol) {
        Symbol sym = visit(symbol.getSymbol());
        return Conditional.when(sym, (org.iguana.datadependent.ast.Expression) symbol.getExpression().accept(this));
    }

    @Override
    public Symbol visit(IfThen symbol) {
        throw new RuntimeException("Unsupported symbol: var-to-int!");
    }

    @Override
    public Symbol visit(IfThenElse symbol) {
        throw new RuntimeException("Unsupported symbol: var-to-int!");
    }

    @Override
    public Symbol visit(Ignore symbol) {
        return Ignore.ignore(visit(symbol.getSymbol()));
    }

    @Override
    public Symbol visit(Nonterminal symbol) {
        java.lang.String variable = symbol.getVariable();

        if (variable != null && !variable.isEmpty())
            current.put(variable, current.size());

        if (symbol.getArguments() == null || symbol.getArguments().length == 0)
            return new Nonterminal.Builder(symbol.getName())
                    .setIndex(symbol.getIndex())
                    .setNodeType(symbol.getNodeType())
                    .setVariable(symbol.getVariable())
                    .addExcepts(symbol.getExcepts())
                    .build();

        org.iguana.datadependent.ast.Expression[] arguments =
            new org.iguana.datadependent.ast.Expression[symbol.getArguments().length];

        int i = 0;
        for (org.iguana.datadependent.ast.Expression e : symbol.getArguments())
            arguments[i++] = (org.iguana.datadependent.ast.Expression) e.accept(this);

        return new Nonterminal.Builder(symbol.getName())
                .setIndex(symbol.getIndex())
                .apply(arguments)
                .setVariable(symbol.getVariable())
                .addExcepts(symbol.getExcepts())
                .build();
    }

    @Override
    public Symbol visit(Offside symbol) {
        return Offside.offside(visit(symbol.getSymbol()));
    }

    @Override
    public Symbol visit(Terminal symbol) {
        return new Terminal.Builder(symbol.getRegularExpression())
                .setNodeType(symbol.getNodeType())
                .setPreConditions(symbol.getPreConditions())
                .setPostConditions(symbol.getPostConditions())
                .setName(symbol.getName())
                .build();
    }

    @Override
    public Symbol visit(While symbol) {
        throw new RuntimeException("Unsupported symbol: var-to-int!");
    }

    @Override
    public Symbol visit(Return symbol) {
        return Return.ret((org.iguana.datadependent.ast.Expression) symbol.getExpression().accept(this));
    }

    @Override
    public Symbol visit(Alt symbol) {
        throw new RuntimeException("Unsupported symbol: var-to-int!");
    }

    @Override
    public Symbol visit(Opt symbol) {
        throw new RuntimeException("Unsupported symbol: var-to-int!");
    }

    @Override
    public Symbol visit(Plus symbol) {
        throw new RuntimeException("Unsupported symbol: var-to-int!");
    }

    @Override
    public Symbol visit(Group symbol) {
        throw new RuntimeException("Unsupported symbol: var-to-int!");
    }

    @Override
    public Symbol visit(Star symbol) {
        throw new RuntimeException("Unsupported symbol: var-to-int!");
    }

    @Override
    public Symbol visit(Start start) {
        throw new RuntimeException("Unsupported symbol: var-to-int!");
    }

    @Override
    public AbstractAST visit(Boolean expression) {
        return expression;
    }

    @Override
    public AbstractAST visit(Integer expression) {
        return expression;
    }

    @Override
    public AbstractAST visit(Real expression) {
        return expression;
    }

    @Override
    public AbstractAST visit(String expression) {
        return expression;
    }

    @Override
    public AbstractAST visit(Not not) {
        return AST.not((org.iguana.datadependent.ast.Expression) not.getExp().accept(this));
    }

    @Override
    public AbstractAST visit(Tuple expression) {
        org.iguana.datadependent.ast.Expression[] expressions =
            new org.iguana.datadependent.ast.Expression[expression.getElements().length];

        int i = 0;
        for (org.iguana.datadependent.ast.Expression e : expression.getElements())
            expressions[i++] = (org.iguana.datadependent.ast.Expression) e.accept(this);

        if (expressions.length == 2 && expressions[0] instanceof Integer && expressions[1] instanceof Integer) {
            return AST.intTuple2((Integer) expressions[0], (Integer) expressions[1]);
        }

        return AST.tuple(expressions);
    }

    @Override
    public AbstractAST visit(Name expression) {
        java.lang.String name = expression.getName();
        java.lang.Integer i = current.get(name);

        if (i == null)
            throw new UndeclaredVariableException(name);

        return AST.var(name, i);
    }

    @Override
    public AbstractAST visit(Call expression) {
        org.iguana.datadependent.ast.Expression[] arguments =
            new org.iguana.datadependent.ast.Expression[expression.getArguments().length];
        int i = 0;
        for (org.iguana.datadependent.ast.Expression e : expression.getArguments())
            arguments[i++] = (org.iguana.datadependent.ast.Expression) e.accept(this);

        java.lang.String name = expression.getFunName();

        switch (name) {
            case "println":
                return AST.println(arguments);
            case "indent":
                return AST.indent(arguments[0]);
            case "ppDeclare":
                return AST.ppDeclare(arguments[0], arguments[1]);
            case "ppLookup":
                return AST.ppLookup(arguments[0]);
            case "endsWith":
                return AST.endsWith(arguments[0], arguments[1]);
            case "startsWith":
                return AST.startsWith(arguments[0]);
            case "not":
                return AST.not(arguments[0]);
            case "neg":
                return AST.neg(arguments[0]);
            case "len":
                return AST.len(arguments[0]);
            case "pr1":
                return AST.pr1(arguments[0], arguments[1], arguments[2]);
            case "pr2":
                org.iguana.datadependent.ast.Expression[] rest =
                    new org.iguana.datadependent.ast.Expression[arguments.length - 2];
                System.arraycopy(arguments, 2, rest, 0, arguments.length - 2);
                return AST.pr2(arguments[0], arguments[1], rest);
            case "pr3":
                return AST.pr3(arguments[0], arguments[1]);
            case "min":
                return AST.min(arguments[0], arguments[1]);
            case "map":
                return AST.map();
            case "put":
                if (arguments.length == 2)
                    return AST.put(arguments[0], arguments[1]);
                else
                    return AST.put(arguments[0], arguments[1], arguments[3]);
            case "contains":
                return AST.contains(arguments[0], arguments[1]);
            case "push":
                return AST.push(arguments[0], arguments[1]);
            case "pop":
                return AST.pop(arguments[0]);
            case "top":
                return AST.top(arguments[0]);
            case "find":
                return AST.find(arguments[0], arguments[1]);
            case "get":
                if (arguments[1] instanceof Integer) {
                    return AST.get(arguments[0], ((Integer) arguments[1]).getValue());
                }
                return AST.get(arguments[0], arguments[1]);
            case "undef":
                return AST.undef();
            case "shift":
                return AST.shift(arguments[0], arguments[1]);
            default:
                throw new UndeclaredVariableException(name);
        }
    }

    @Override
    public AbstractAST visit(Assignment expression) {

        java.lang.String id = expression.getId();
        java.lang.Integer i = current.get(id);

        if (i == null)
            throw new UndeclaredVariableException(id);

        return AST.assign(id, i, (org.iguana.datadependent.ast.Expression) expression.getExpression().accept(this));
    }

    @Override
    public AbstractAST visit(LShiftANDEqZero expression) {
        return AST.lShiftANDEqZero((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(OrIndent expression) {
        return AST.orIndent((org.iguana.datadependent.ast.Expression) expression.getIndex().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getIndent().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getFirst().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getLExt().accept(this));
    }

    @Override
    public AbstractAST visit(AndIndent expression) {
        return AST.andIndent((org.iguana.datadependent.ast.Expression) expression.getIndex().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getFirst().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getLExt().accept(this));
    }

    @Override
    public AbstractAST visit(Or expression) {
        return AST.or((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(And expression) {
        return AST.and((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(Less expression) {
        return AST.less((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(LessThanEqual expression) {
        return AST.lessEq((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(Greater expression) {
        return AST.greater((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(GreaterThanEqual expression) {
        return AST.greaterEq((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(Equal expression) {
        return AST.equal((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(NotEqual expression) {
        return AST.notEqual((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(LeftExtent expression) {
        throw new RuntimeException("Unsupported yet expression: " + expression);
    }

    @Override
    public AbstractAST visit(RightExtent expression) {
        throw new RuntimeException("Unsupported yet expression: " + expression);
    }

    @Override
    public AbstractAST visit(Yield expression) {
        java.lang.String label = expression.getLabel();
        java.lang.Integer i = current.get(label);

        if (i == null)
            throw new UndeclaredVariableException(label);

        return AST.yield(label, i);
    }

    @Override
    public AbstractAST visit(Val expression) {
        throw new RuntimeException("Unsupported yet expression: " + expression);
    }

    @Override
    public AbstractAST visit(EndOfFile expression) {
        return expression;
    }

    @Override
    public AbstractAST visit(org.iguana.datadependent.ast.Expression.IfThenElse expression) {
        return AST.ifThenElse((org.iguana.datadependent.ast.Expression) expression.getCondition().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getThenPart().accept(this),
                (org.iguana.datadependent.ast.Expression) expression.getElsePart().accept(this));
    }

    @Override
    public AbstractAST visit(VariableDeclaration declaration) {
        int i = current.size();
        current.put(declaration.getName(), i);
        return AST.varDecl(declaration.getName(), i,
            (org.iguana.datadependent.ast.Expression) declaration.getExpression().accept(this));
    }

    @Override
    public AbstractAST visit(org.iguana.datadependent.ast.Statement.VariableDeclaration declaration) {
        return AST.varDeclStat((VariableDeclaration) declaration.getDeclaration().accept(this));
    }

    @Override
    public AbstractAST visit(Add expression) {
        return AST.and((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
            (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(Subtract expression) {
        return AST.subtract((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
            (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));

    }

    @Override
    public AbstractAST visit(Multiply expression) {
        return AST.multiply((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
            (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
    }

    @Override
    public AbstractAST visit(Divide expression) {
        return AST.divide((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this),
            (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));

    }

    @Override
    public AbstractAST visit(Expression statement) {
        return AST.stat((org.iguana.datadependent.ast.Expression) statement.getExpression().accept(this));
    }

    @Override
    public Condition visit(DataDependentCondition condition) {
        return DataDependentCondition.predicate(
            (org.iguana.datadependent.ast.Expression) condition.getExpression().accept(this));
    }

    @Override
    public Condition visit(PositionalCondition condition) {
        return condition;
    }

    @Override
    public Condition visit(RegularExpressionCondition condition) {
        return condition;
    }

}
