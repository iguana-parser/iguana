package org.iguana.iggy;

import iguana.regex.*;
import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Alt;
import org.iguana.grammar.symbol.Opt;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Star;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.parsetree.ParseTreeVisitor;

import java.util.*;
import java.util.stream.Collectors;

public class IggyToGrammarVisitor implements ParseTreeVisitor {

    private final Map<String, RegularExpression> terminalsMap = new HashMap<>();
    private String start;
    private org.iguana.grammar.symbol.Identifier layout;
    private final Map<String, Object> globals = new HashMap<>();

    @Override
    public Object visitNonterminalNode(NonterminalNode node) {
        switch (node.getName()) {
            case "Definition":
                return visitDefinition(node);

            case "Rule":
                return visitRule(node);

            case "Parameters":
                return visitParameters(node);

            case "PriorityLevels":
                return visitPriorityLevels(node);

            case "Alternative":
                return visitAlternative(node);

            case "Sequence":
                return visitSequence(node);

            case "Label":
                return visitLabel(node);

            case "Symbol":
                return visitSymbol(node);

            case "Binding":
                return visitBinding(node);

            case "Regex":
                return visitRegex(node);

            case "CharClass":
                return visitCharClass(node);

            case "Range":
                return visitRange(node);

            case "Expression":
                return visitExpression(node);

            case "Statement":
                return visitStatement(node);

            case "Global":
                return visitGlobal(node);

            case "Initializer":
                return visitInitializer(node);

            case "MapEntry":
                return visitMapEntry(node);
        }

        return visitChildren(node);
    }

    /*
     * Definition = (Rule | Global)+;
     */
    private Grammar visitDefinition(NonterminalNode node) {
        Grammar.Builder builder = new Grammar.Builder();
        List<Rule> rules = (List<Rule>) node.childAt(0).accept(this);
        for (Rule rule : rules) {
            if (rule != null) { // null means a global definition
                builder.addRule(rule);
            }
        }
        for (Map.Entry<String, RegularExpression> entry : terminalsMap.entrySet()) {
            builder.addTerminal(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Object> entry : globals.entrySet()) {
            builder.addGlobal(entry.getKey(), entry.getValue());
        }
        builder.setStartSymbol(Start.from(start));
        builder.setLayout(layout);
        return builder.build();
    }

    /*
     * Rule : "start"? Identifier Parameters? ":" Body          %Syntax
     *      | "layout"? "terminal" Identifier ":" RegexBody     %Lexical
     *      ;
     */
    private Rule visitRule(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "Syntax":
                Identifier nonterminalName = getIdentifier(node.getChildWithName("Identifier"));
                List<String> parameters = (List<String>) node.getChildWithName("Parameters?").accept(this);
                if (parameters == null) parameters = Collections.emptyList();
                List<PriorityLevel> priorityLevels = (List<PriorityLevel>) node.getChildWithName("Body").accept(this);

                if (!node.childAt(0).children().isEmpty()) { // start symbol
                    start = nonterminalName.getName();
                }

                Nonterminal nonterminal = new Nonterminal.Builder(nonterminalName.getName()).addParameters(parameters).build();
                return new Rule.Builder(nonterminal).addPriorityLevels(priorityLevels).build();

            // RegexBody : { RegexSequence "|" }*;
            // RegexSequence : Regex+;
            case "Lexical":
                List<List<RegularExpression>> alts = (List<List<RegularExpression>>) node.getChildWithName("RegexBody").accept(this);
                Identifier identifier = getIdentifier(node.getChildWithName("Identifier"));
                terminalsMap.put(identifier.getName(), getRegex(alts));

                if (!node.childAt(0).children().isEmpty()) {
                    layout = identifier;
                }

                return null;

            default:
                throw new RuntimeException("Unexpected label");
        }
    }

    private static RegularExpression getRegex(List<List<RegularExpression>> listOfList) {
        if (listOfList.size() == 1) {
            return getRegexOfList(listOfList.get(0));
        }
        iguana.regex.Alt.Builder<RegularExpression> builder = new iguana.regex.Alt.Builder<>();
        for (List<RegularExpression> list : listOfList) {
            builder.add(getRegexOfList(list));
        }
        return builder.build();
    }

    private static RegularExpression getRegexOfList(List<RegularExpression> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        return iguana.regex.Seq.from(list);
    }

    /*
     * Parameters: "(" { Identifier "," }* ")";
     */
    private List<String> visitParameters(NonterminalNode node) {
        return node.childAt(1).children().stream()
            .filter(s -> !s.getText().equals(","))
            .map(s -> s.getText().substring(1)).collect(Collectors.toList());
    }

    /*
     * Alternatives: { Alternative '|' }+
     */
    private PriorityLevel visitPriorityLevels(NonterminalNode node) {
        PriorityLevel.Builder builder = new PriorityLevel.Builder();
        builder.addAlternatives((List<Alternative>) node.childAt(0).accept(this));
        return builder.build();
    }

    /*
     * Alternative
     *   : Sequence                                             %Sequence
     *   | Associativity "(" Sequence ("|" Sequence)+ ")"       %Assoc
     *   | Label?                                               %Empty
     *   ;
     */
    private Alternative visitAlternative(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "Sequence": {
                Alternative.Builder builder = new Alternative.Builder();
                builder.addSequence((Sequence) node.childAt(0).accept(this));
                return builder.build();
            }

            case "Assoc": {
                Associativity associativity = getAssociativity(node.childAt(0));
                List<Sequence> seqs = new ArrayList<>();
                seqs.add((Sequence) node.childAt(2).accept(this));
                seqs.addAll((List<Sequence>) node.childAt(3).accept(this));
                return new Alternative.Builder(seqs, associativity).build();
            }

            case "Empty":
                String label = (String) node.childAt(0).accept(this);
                if (label != null) {
                    Sequence sequence = new Sequence.Builder().setLabel(label).build();
                    return new Alternative.Builder().addSequence(sequence).build();
                } else {
                    return new Alternative.Builder().build();
                }

            default:
                throw new RuntimeException("Unexpected label");
        }
    }

    /*
     * Sequence: Associativity? PreCondition? Symbol Symbol+ ReturnExpression? Label?     %MoreThanOne
     *         | PreCondition? Symbol ReturnExpression? Label?                            %Single
     *         ;
     */
    private Sequence visitSequence(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "MoreThanOne": {
                Associativity associativity = null;
                if (!node.childAt(0).children().isEmpty()) {
                    associativity = getAssociativity(node.childAt(0).childAt(0));
                }
                Sequence.Builder builder = new Sequence.Builder();
                List<Expression> expressions = (List<Expression>) node.childAt(1).accept(this);
                List<Symbol> symbols = new ArrayList<>();
                Symbol symbol = (Symbol) node.childAt(2).accept(this);
                SymbolBuilder<? extends Symbol> symbolBuilder = symbol.copy();
                if (expressions != null) {
                    for (Expression expression : expressions) {
                        symbolBuilder.addPreCondition(DataDependentCondition.predicate(expression));
                    }
                }
                symbols.add(symbolBuilder.build());
                symbols.addAll((List<Symbol>) node.childAt(3).accept(this));
                builder.addSymbols(symbols);
                Expression returnExpression = (Expression) node.childAt(4).accept(this);
                if (returnExpression != null) {
                    builder.addSymbol(Return.ret(returnExpression));
                }
                String label = (String) node.childAt(5).accept(this);
                builder.setAssociativity(associativity);
                builder.setLabel(label);
                return builder.build();
            }

            case "Single": {
                Sequence.Builder builder = new Sequence.Builder();
                List<Expression> expressions = (List<Expression>) node.childAt(0).accept(this);
                Symbol symbol = (Symbol) node.childAt(1).accept(this);
                SymbolBuilder<? extends Symbol> symbolBuilder = symbol.copy();
                if (expressions != null) {
                    for (Expression expression : expressions) {
                        symbolBuilder.addPreCondition(DataDependentCondition.predicate(expression));
                    }
                }
                Expression returnExpression = (Expression) node.childAt(2).accept(this);
                if (returnExpression != null) {
                    builder.addSymbol(Return.ret(returnExpression));
                }
                builder.addSymbol(symbolBuilder.build());
                String label = (String) node.childAt(3).accept(this);
                builder.setLabel(label);
                return builder.build();
            }

            default:
                throw new RuntimeException("Unexpected label: " + node.getGrammarDefinition().getLabel());
        }
    }

    /*
     * Label: "%" Identifier
     */
    private String visitLabel(NonterminalNode node) {
        return getIdentifier(node.childAt(1)).getName();
    }

    /*
     * Symbol
     *   : Identifier Arguments                   %Call
     *   > "offside" Symbol                       %Offside
     *   > Symbol "*"                             %Star
     *   | Symbol "+"                             %Plus
     *   | Symbol "?"                             %Option
     *   | "(" Symbol Symbol+ ")"                 %Sequence
     *   | "(" Symbol+ ("|" Symbol+)+ ")"         %Alternation
     *   > "align" Symbol                         %Align
     *   | "ignore" Symbol                        %Ignore
     *   | "if" Expression Symbol "else" Symbol   %IfThenElse
     *   > Identifier ":" Symbol                  %Labeled
     *   | Symbol "[" {Expression ","}+ "]"       %Constraint
     *   | "{" {Binding ","}+ "}"                 %Bindings
     *   | Regex "<<" Symbol                      %Precede
     *   | Regex "!<<" Symbol                     %NotPrecede
     *   > Symbol ">>" Regex                      %Follow
     *   | Symbol "!>>" Regex                     %NotFollow
     *   | Symbol "\\" Regex                      %Exclude
     *   | Symbol "!" Identifier                  %Except
     *   | Identifier                             %Nont
     *   | String                                 %String
     *   | Char                                   %Character
     *   | CharClass                              %CharClass
     *   | "{" Symbol Symbol+ "}" "*"             %StarSep
     *   | "{" Symbol Symbol+ "}" "+"             %PlusSep
     *   | Symbol Expression                      %Statement
     *   ;
     */
    private Symbol visitSymbol(NonterminalNode node) {
        String label = node.getGrammarDefinition().getLabel();

        switch (label) {
            case "Call": {
                Expression[] expressions = ((List<Expression>) node.childAt(1).accept(this)).toArray(new Expression[]{});
                return new Nonterminal.Builder(getIdentifier(node.childAt(0)).getName()).apply(expressions).build();
            }

            case "Offside":
                return Offside.offside((Symbol) node.childAt(1).accept(this));

            case "Star":
                return Star.from((Symbol) node.childAt(0).accept(this));

            case "Plus":
                return Plus.from((Symbol) node.childAt(0).accept(this));

            case "Sequence": {
                List<Symbol> symbols = new ArrayList<>();
                symbols.add((Symbol) node.childAt(1).accept(this));
                symbols.addAll((List<? extends Symbol>) node.childAt(2).accept(this));
                return Group.from(symbols);
            }

            case "Alternation": {
                List<Symbol> symbols = new ArrayList<>();
                List<Symbol> first = (List<Symbol>) node.childAt(1).accept(this);
                List<List<Symbol>> second = (List<List<Symbol>>) node.childAt(2).accept(this);
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

            case "Option":
                return Opt.from((Symbol) node.childAt(0).accept(this));

            case "Align":
                return Align.align((Symbol) node.childAt(0).accept(this));

            case "Ignore":
                return Ignore.ignore((Symbol) node.childAt(0).accept(this));

            case "IfThenElse":
                return IfThenElse.ifThenElse(
                        (Expression) node.childAt(1).accept(this),
                        (Symbol) node.childAt(2).accept(this),
                        (Symbol) node.childAt(4).accept(this)
                );

            case "Labeled": {
                Symbol symbol = (Symbol) node.childAt(2).accept(this);
                return symbol.copy().setLabel(getIdentifier(node.childAt(0)).getName()).build();
            }

            case "Precede": {
                Symbol symbol = (Symbol) node.childAt(2).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(0).accept(this);
                return symbol.copy().addPreCondition(RegularExpressionCondition.precede(regex)).build();
            }

            case "NotPrecede": {
                Symbol symbol = (Symbol) node.childAt(2).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(0).accept(this);
                return symbol.copy().addPreCondition(RegularExpressionCondition.notPrecede(regex)).build();
            }

            case "Follow": {
                Symbol symbol = (Symbol) node.childAt(0).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
                return symbol.copy().addPostCondition(RegularExpressionCondition.follow(regex)).build();
            }

            case "NotFollow": {
                Symbol symbol = (Symbol) node.childAt(0).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
                return symbol.copy().addPostCondition(RegularExpressionCondition.notFollow(regex)).build();
            }

            case "Exclude": {
                Symbol symbol = (Symbol) node.childAt(0).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
                return symbol.copy().addPostCondition(RegularExpressionCondition.notMatch(regex)).build();
            }

            case "Except": {
                Identifier symbol = (Identifier) node.childAt(0).accept(this);
                return symbol.copy().addExcept(getIdentifier(node.childAt(2)).getName()).build();
            }

            case "Nont":
                return org.iguana.grammar.symbol.Identifier.fromName(getIdentifier(node).getName());

            case "String":
            case "Character":
                return Terminal.from(getCharsRegex(node.getText()));

            case "CharClass":
                return Terminal.from((iguana.regex.Alt<RegularExpression>) node.childAt(0).accept(this));

            case "StarSep": {
                Symbol symbol = (Symbol) node.childAt(1).accept(this);
                List<Symbol> seps = (List<Symbol>) node.childAt(2).accept(this);
                return new Star.Builder(symbol).addSeparators(seps).build();
            }

            case "PlusSep": {
                Symbol symbol = (Symbol) node.childAt(1).accept(this);
                List<Symbol> seps = (List<Symbol>) node.childAt(2).accept(this);
                return new Plus.Builder(symbol).addSeparators(seps).build();
            }

            case "Statement": {
                Symbol symbol = (Symbol) node.childAt(0).accept(this);
                List<Statement> statements = (List<Statement>) node.childAt(1).accept(this);
                return Code.code(symbol, statements.toArray(new Statement[0]));
            }

            case "PostCondition": {
                Symbol symbol = (Symbol) node.childAt(0).accept(this);
                List<Expression> expressions = (List<Expression>) node.childAt(2).accept(this);
                SymbolBuilder<? extends Symbol> builder = symbol.copy();
                for (Expression expression : expressions) {
                    builder.addPostCondition(DataDependentCondition.predicate(expression));
                }
                return builder.build();
             }

            default:
                throw new RuntimeException("Unexpected label: " + label);
        }
    }

    /**
     * Binding: Identifier "=" Expression        %Assign
     *        | "var" Identifier "=" Expression  %Declare
     */
    private Statement visitBinding(NonterminalNode node) {
        String label = node.getGrammarDefinition().getLabel();
        switch (label) {
            case "Assign":
                return AST.stat(AST.assign(getIdentifier(node.childAt(0)).getName(), (Expression) node.childAt(2).accept(this)));

            case "Declare":
                Expression expression = (Expression) node.childAt(3).accept(this);
                return AST.varDeclStat(getIdentifier(node.childAt(1)).getName(), expression);

            default:
                throw new RuntimeException("Unexpected label: " + label);
        }
    }

    /**
     * Regex
     *  : Regex "*"                     %Star
     *  | Regex "+"                     %Plus
     *  | Regex "?"                     %Option
     *  | "(" Regex ")"                 %Bracket
     *  | "(" Regex Regex+ ")"          %Sequence
     *  | "(" Regex+ ("|" Regex+)+ ")"  %Alternation
     *  | Identifier                    %Reference
     *  | CharClass                     %CharClass
     *  | String                        %String
     *  | Char                          %Char
     */
    private RegularExpression visitRegex(NonterminalNode node) {
        String label = node.getGrammarDefinition().getLabel();
        switch (label) {
            case "Star":
                return iguana.regex.Star.from((RegularExpression) node.childAt(0).accept(this));

            case "Plus":
                return iguana.regex.Plus.from((RegularExpression) node.childAt(0).accept(this));

            case "Option":
                return iguana.regex.Opt.from((RegularExpression) node.childAt(0).accept(this));

            case "Bracket":
                return (RegularExpression) node.childAt(1).accept(this);

            case "Sequence": {
                List<RegularExpression> list = new ArrayList<>();
                list.add((RegularExpression) node.childAt(1).accept(this));
                list.addAll((Collection<? extends RegularExpression>) node.childAt(2).accept(this));
                return iguana.regex.Seq.from(list);
            }

            case "Alternation": {
                List<RegularExpression> list = new ArrayList<>();
                List<RegularExpression> first = (List<RegularExpression>) node.childAt(1).accept(this);
                if (first.size() == 1) {
                    list.add(first.get(0));
                } else {
                    list.add(iguana.regex.Seq.from(first));
                }
                List<List<RegularExpression>> second = (List<List<RegularExpression>>) node.childAt(2).accept(this);
                for (List<RegularExpression> l : second) {
                    if (l.size() == 1) {
                        list.add(l.get(0));
                    } else {
                        list.add(iguana.regex.Seq.from(l));
                    }
                }
                return iguana.regex.Alt.from(list);
            }

            case "Nont":
                return iguana.regex.Reference.from(getIdentifier(node.childAt(0)).getName());

            case "CharClass":
                return (iguana.regex.Alt<RegularExpression>) node.childAt(0).accept(this);

            // String: String
            case "String":
                return getCharsRegex(node.getText());

            // Char = Char
            case "Character": {
                String s = node.getText();
                int[] chars = getChars(s.substring(1, s.length() - 1));
                if (chars.length == 0) {
                    throw new RuntimeException("Length must be positive");
                }
                if (chars.length == 1)
                    return Char.from(chars[0]);
                return iguana.regex.Seq.from(chars);
            }

            default:
                throw new RuntimeException("Unexpected label: " + label);
        }
    }

    /*
     * CharClass
     *   : '[' Range* ']'    #Chars
     *   | '!' '[' Range* ']'   #NotChars
     *   ;
     */
    private iguana.regex.Alt visitCharClass(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "Chars":
                return iguana.regex.Alt.from((List<RegularExpression>) node.childAt(1).accept(this));

            case "NotChars":
                return iguana.regex.Alt.not((List<RegularExpression>) node.childAt(2).accept(this));

            default:
                throw new RuntimeException("Unexpected label");
        }
    }

    /*
     * Range
     *  : RangeChar "-" RangeChar #Range
     *  | RangeChar               #Character
     *  ;
     */
    private RegularExpression visitRange(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "Range": {
                int start = getRangeChar(node.childAt(0).getText());
                int end = getRangeChar(node.childAt(2).getText());
                return CharRange.in(start, end);
            }

            case "Character":
                int c = getRangeChar(node.childAt(0).getText());
                return Char.from(c);

            default:
                throw new RuntimeException("Unexpected label");
        }
    }

    /*
     * Statement
     *   = Expression    %Expression
     *   | Binding       %Binding
     */
    private Statement visitStatement(NonterminalNode node) {
        String label = node.getGrammarDefinition().getLabel();
        switch (label) {
            case "Expression":
                Expression expression = (Expression) node.childAt(0).accept(this);
                return AST.stat(expression);

            case "Binding":
                return (Statement) node.childAt(0).accept(this);

            default:
                throw new RuntimeException("Unexpected label: " + label);
        }
    }

    /**
     * Global
     *   = "global" Identifier "=" Initializer
     */
    private Object visitGlobal(NonterminalNode node) {
        String key = node.childAt(1).getText();
        Object value = node.childAt(3).accept(this);
        globals.put(key, value);
        return null;
    }

    /**
     * Initializer
     *   = "null"                       %Null
     *   | Number                       %Number
     *   | String                       %String
     *   | '[' {Initializer ','}* ']'   %List
     *   | '{' {MapEntry ','}* '}'      %Map
     */
    private Object visitInitializer(NonterminalNode node) {
        String label = node.getGrammarDefinition().getLabel();
        switch (label) {
            case "Null":
                return null;

            case "Number":
                return Integer.parseInt(node.childAt(0).getText());

            case "String":
                return node.childAt(1).getText();

            case "List":
                List<Object> list = new ArrayList<>();
                List<Object> elements = (List<Object>) node.childAt(1).accept(this);
                if (elements != null) {
                    list.addAll(elements);
                }
                return list;

            case "Map":
                Map<String, Object> map = new HashMap<>();
                List<Map.Entry<String, Object>> entries = (List<Map.Entry<String, Object>> ) node.childAt(1).accept(this);
                if (entries != null) {
                    for (Map.Entry<String, Object> entry : entries) {
                        map.put(entry.getKey(), entry.getValue());
                    }
                }
                return map;

            default:
                throw new RuntimeException("Unexpected label: " + label);
        }
    }

    /**
     * MapEntry
     *   = Identifier '=' Initializer
     */
    private Map.Entry<String, Object> visitMapEntry(NonterminalNode node) {
        return new Map.Entry<String, Object>() {
            @Override
            public String getKey() {
                return node.childAt(0).getText();
            }

            @Override
            public Object getValue() {
                return node.childAt(2).accept(IggyToGrammarVisitor.this);
            }

            @Override
            public Object setValue(Object value) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /*
     * Expression
     * :           Expression Arguments           %Call
     * > left      (Expression "*" Expression     %Multiplication
     * |            Expression "/" Expression     %Division)
     * > left      (Expression "+" Expression     %Plus
     * |            Expression "-" Expression     %Minus)
     * > non-assoc (Expression "\>=" Expression   %GreaterEq
     * |            Expression "\<=" Expression   %LessEq
     * |            Expression "\>" Expression    %Greater
     * |            Expression "\<" Expression    %Less)
     * > non-assoc (Expression "==" Expression    %Equal
     * |            Expression "!=" Expression    %NotEqual)
     * > left      (Expression "&&" Expression    %And
     * |            Expression "||" Expression    %Or)
     * |           Identifier ".l"                %LExtent
     * |           Identifier ".r"                %RExtent
     * |           Identifier ".yield"            %Yield
     * |           Identifier ".val"              %Val
     * |           VarName                        %Name
     * |           Number                         %Number
     * |           "(" Expression ")"             %Bracket
     * ;
     */
    private Expression visitExpression(NonterminalNode node) {
        String label = node.getGrammarDefinition().getLabel();
        switch (label) {
            case "Call":
                String funName = node.childAt(0).getText();
                Expression[] arguments = ((List<Expression>) node.childAt(1).accept(this)).toArray(new Expression[]{});
                switch (funName) {
                    case "println":
                        return AST.println(arguments);
                    case "indent":
                        return AST.indent(arguments[0]);
                    default:
                        throw new RuntimeException("Unknown function name: " + funName);
                }

            case "GreaterEq": {
                Expression lhs = (Expression) node.childAt(0).accept(this);
                Expression rhs = (Expression) node.childAt(2).accept(this);
                return AST.greaterEq(lhs, rhs);
            }

            case "LessEq": {
                Expression lhs = (Expression) node.childAt(0).accept(this);
                Expression rhs = (Expression) node.childAt(2).accept(this);
                return AST.lessEq(lhs, rhs);
            }

            case "Less": {
                Expression lhs = (Expression) node.childAt(0).accept(this);
                Expression rhs = (Expression) node.childAt(2).accept(this);
                return AST.less(lhs, rhs);
            }

            case "Greater": {
                Expression lhs = (Expression) node.childAt(0).accept(this);
                Expression rhs = (Expression) node.childAt(2).accept(this);
                return AST.greater(lhs, rhs);
            }

            case "Equal": {
                Expression lhs = (Expression) node.childAt(0).accept(this);
                Expression rhs = (Expression) node.childAt(2).accept(this);
                return AST.equal(lhs, rhs);
            }

            case "NotEqual": {
                Expression lhs = (Expression) node.childAt(0).accept(this);
                Expression rhs = (Expression) node.childAt(2).accept(this);
                return AST.notEqual(lhs, rhs);
            }

            case "And": {
                Expression lhs = (Expression) node.childAt(0).accept(this);
                Expression rhs = (Expression) node.childAt(2).accept(this);
                return AST.and(lhs, rhs);
            }

            case "Or": {
                Expression lhs = (Expression) node.childAt(0).accept(this);
                Expression rhs = (Expression) node.childAt(2).accept(this);
                return AST.or(lhs, rhs);
            }

            case "LExtent":
                String l = node.childAt(0).getText();
                return AST.lExt(l);

            case "RExtent":
                String r = node.childAt(0).getText();
                return AST.rExt(r);

            case "Yield":
                String yield = node.childAt(0).getText();
                return AST.yield(yield);

            case "Val":
                String val = node.childAt(0).getText();
                return AST.val(val);

            case "Name":
                return AST.var((node.childAt(0).getText()).substring(1));

            case "Number":
                return AST.integer(Integer.parseInt(node.childAt(0).getText()));

            case "Bracket":
                return (Expression) node.childAt(1).accept(this);

            default:
                throw new RuntimeException("Unexpected label: " +  label);
        }
    }

    private Associativity getAssociativity(ParseTreeNode node) {
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

    private Identifier getIdentifier(ParseTreeNode node) {
        return Identifier.fromName(node.getText());
    }

    private static int getRangeChar(String s) {
        switch (s) {
            case "\\n": return '\n';
            case "\\r": return '\r';
            case "\\t": return '\t';
            case "\\f": return '\f';
            case "\\'": return '\'';
            case "\\\"": return '\"';
            case "\\ ": return ' ';
            case "\\[": return '[';
            case "\\]": return ']';
            case "\\-": return '-';
        }
        return s.charAt(0);
    }

    private RegularExpression getCharsRegex(String s) {
        int[] chars = getChars(s.substring(1, s.length() - 1));
        if (chars.length == 1) {
            return Char.from(chars[0]);
        }
        return Seq.from(chars);
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
}
