package org.iguana.iggy;

import iguana.regex.Char;
import iguana.regex.CharRange;
import iguana.regex.RegularExpression;
import iguana.regex.Seq;
import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.*;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.parsetree.ParseTreeVisitor;

import java.util.*;
import java.util.stream.Collectors;

public class IggyToGrammarVisitor implements ParseTreeVisitor {

    private Map<String, RegularExpression> terminalsMap = new HashMap<>();
    private Nonterminal startNonterminal;

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
        }

        return visitChildren(node);
    }

    /*
     * Definition: Rule+;
     */
    private Grammar visitDefinition(NonterminalNode node) {
        Grammar.Builder builder = new Grammar.Builder();
        for (Rule rule : (List<Rule>) node.childAt(0).accept(this)) {
            builder.addRule(rule);
        }
        for (Map.Entry<String, RegularExpression> entry : terminalsMap.entrySet()) {
            builder.addTerminal(entry.getKey(), entry.getValue());
        }
        builder.setStartSymbol(Start.from(startNonterminal));
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
                List<Identifier> parameters = (List<Identifier>) node.childAt(1).accept(this);
                if (parameters == null) parameters = Collections.emptyList();
                List<PriorityLevel> priorityLevels = (List<PriorityLevel>) node.getChildWithName("Body").accept(this);
                List<String> params = parameters.stream().map(p -> p.id).collect(Collectors.toList());
                Nonterminal nonterminal = new Nonterminal.Builder(nonterminalName.id).addParameters(params).build();
                if (!node.childAt(0).children().isEmpty()) {
                    startNonterminal = nonterminal;
                }
                return new Rule.Builder(nonterminal).addPriorityLevels(priorityLevels).build();

            // RegexBody : { RegexSequence "|" }*;
            // RegexSequence : Regex+;
            case "Lexical":
                List<List<RegularExpression>> alts = (List<List<RegularExpression>>) node.getChildWithName("RegexBody").accept(this);
                Identifier name = getIdentifier(node.getChildWithName("Identifier"));
                terminalsMap.put(name.id, getRegex(alts));
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
    private List<Identifier> visitParameters(NonterminalNode node) {
        return (List<Identifier>) node.childAt(1).accept(this);
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
     *   |                                                      %Empty
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
                Associativity associativity = getAssociativity(node.childAt(0).childAt(0));
                List<Sequence> seqs = new ArrayList<>();
                seqs.add((Sequence) node.childAt(2).accept(this));
                seqs.addAll((List<Sequence>) node.childAt(3).accept(this));
                return new Alternative(seqs, associativity);
            }

            case "Empty":
                return new Alternative.Builder().build();

            default:
                throw new RuntimeException("Unexpected label");
        }
    }

    /*
     * Sequence: Associativity? Symbol Symbol+ ReturnExpression? Label?     %MoreThanOne
     *         | Symbol ReturnExpression? Label?                            %Single
     *         ;
     */
    private Sequence visitSequence(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "MoreThanOne": {
                Associativity associativity = null;
                if (!node.childAt(0).children().isEmpty()) {
                    associativity = getAssociativity(node.childAt(0).childAt(0));
                }
                Symbol first = (Symbol) node.childAt(1).accept(this);
                List<Symbol> rest = (List<Symbol>) node.childAt(2).accept(this);
                Expression returnExpression = (Expression) node.childAt(3).accept(this);
                if (returnExpression != null) {
                    rest.add(Return.ret(returnExpression));
                }
                String label = (String) node.childAt(4).accept(this);
                return new Sequence(first, rest, associativity, label);
            }

            case "Single": {
                Symbol first = (Symbol) node.childAt(0).accept(this);
                List<Symbol> rest = new ArrayList<>();
                Expression returnExpression = (Expression) node.childAt(1).accept(this);
                if (returnExpression != null) {
                    rest.add(Return.ret(returnExpression));
                }
                String label = (String) node.childAt(2).accept(this);
                return new Sequence(first, rest, null, label);
            }

            default:
                throw new RuntimeException("Unexpected label");
        }
    }

    /*
     * Label: "%" Identifier
     */
    private String visitLabel(NonterminalNode node) {
        return getIdentifier(node.childAt(1)).id;
    }

    /*
     * Symbol
     *   : Identifier Arguments             %Call
     *   > "offside" Symbol                 %Offside
     *   > Symbol "*"                       %Star
     *   | Symbol "+"                       %Plus
     *   | Symbol "?"                       %Option
     *   | "(" Symbol Symbol+ ")"           %Sequence
     *   | "(" Symbol+ ("|" Symbol+)+ ")"   %Alternation
     *   > "align" Symbol                   %Align
     *   | "ignore" Symbol                  %Ignore
     *   | Expression "?" Symbol ":" Symbol %IfThenElse
     *   > Identifier ":" Symbol            %Labeled
     *   | "[" {Expression ","}+ "]"        %Constraints
     *   | "{" {Binding ","}+ "}"           %Bindings
     *   | Regex "<<" Symbol                %Precede
     *   | Regex "!<<" Symbol               %NotPrecede
     *   > Symbol ">>" Regex                %Follow
     *   | Symbol "!>>" Regex               %NotFollow
     *   | Symbol "\\" Regex                %Exclude
     *   | Symbol "!" Identifier            %Except
     *   | Identifier                       %Nont
     *   | String                           %String
     *   | Char                             %Character
     *   | "{" Symbol Symbol+ "}" "*"       %StarSep
     *   | "{" Symbol Symbol+ "}" "+"       %PlusSep
     *   ;
     */
    private Symbol visitSymbol(NonterminalNode node) {
        String label = node.getGrammarDefinition().getLabel();

        switch (label) {
            case "Call": {
                Expression[] expressions = ((List<Expression>) node.childAt(0).accept(this)).toArray(new Expression[]{});
                return new Nonterminal.Builder(getIdentifier(node.childAt(0)).id).apply(expressions).build();
            }

            case "Offside":
                return Offside.offside((Symbol) node.childAt(0).accept(this));

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
                        (Expression) node.childAt(0).accept(this),
                        (Symbol) node.childAt(1).accept(this),
                        (Symbol) node.childAt(2).accept(this)
                );

            case "Labeled": {
                Symbol symbol = (Symbol) node.childAt(1).accept(this);
                return symbol.copyBuilder().setLabel(getIdentifier(node.childAt(0)).id).build();
            }

            case "Constraints": {
                List<Expression> expressions = (List<Expression>) node.childAt(0).accept(this);
                CodeHolder codeHolder = new CodeHolder(null, expressions);
                return codeHolder;
            }

            case "Bindings": {
                List<Object> objects = (List<Object>) node.childAt(1).accept(this);
                List<Expression> expressions = new ArrayList<>();
                List<Statement> statements = new ArrayList<>();
                for (Object object : objects) {
                    if (object instanceof Expression) {
                        expressions.add((Expression) object);
                    } else {
                        statements.add((Statement) object);
                    }
                }
                return new CodeHolder(statements, expressions);
            }

            case "Precede": {
                Symbol symbol = (Symbol) node.childAt(2).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(0).accept(this);
                return symbol.copyBuilder().addPreCondition(RegularExpressionCondition.precede(regex)).build();
            }

            case "NotPrecede": {
                Symbol symbol = (Symbol) node.childAt(2).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(0).accept(this);
                return symbol.copyBuilder().addPreCondition(RegularExpressionCondition.notPrecede(regex)).build();
            }

            case "Follow": {
                Symbol symbol = (Symbol) node.childAt(0).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
                return symbol.copyBuilder().addPostCondition(RegularExpressionCondition.follow(regex)).build();
            }

            case "NotFollow": {
                Symbol symbol = (Symbol) node.childAt(0).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
                return symbol.copyBuilder().addPostCondition(RegularExpressionCondition.notFollow(regex)).build();
            }

            case "Exclude": {
                Symbol symbol = (Symbol) node.childAt(0).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
                return symbol.copyBuilder().addPostCondition(RegularExpressionCondition.notMatch(regex)).build();
            }

            case "Except": {
                Nonterminal symbol = (Nonterminal) node.childAt(0).accept(this);
                return symbol.copyBuilder().addExcept(getIdentifier(node.childAt(2)).id).build();
            }

            case "Nont":
                return org.iguana.grammar.symbol.Identifier.fromName(getIdentifier(node).id);

            case "String":
            case "Character":
                return Terminal.from(getCharsRegex(node.getText()));

            case "StarSep": {
                Symbol symbol = (Symbol) node.childAt(1).accept(this);
                List<Symbol> seps = (List<Symbol>) node.childAt(2).accept(this);
                return new Star.Builder(symbol).addSeparators(seps).build();
            }

            case "PlusSep": {
                Symbol symbol = (Symbol) node.childAt(1).accept(this);
                List<Symbol> seps = (List<Symbol>) node.childAt(2).accept(this);
                return Plus.builder(symbol).addSeparators(seps).build();
            }

            default:
                throw new RuntimeException("Unexpected label: " + label);
        }
    }

    /**
     * Binding: Identifier "=" Expression        %Assign
     *        | "var" Identifier "=" Expression  %Declare
     */
    private Object visitBinding(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "Assign":
                return AST.assign(getIdentifier(node.childAt(0)).id, (Expression) node.childAt(1).accept(this));

            case "Declare": {
                Expression expression = (Expression) node.childAt(3).accept(this);
                return AST.varDeclStat(getIdentifier(node.childAt(1)).id, expression);
            }

            default:
                throw new RuntimeException("Unexpected label");
        }
    }

    /**
     * Regex
     *  : Regex "*"                     %Star
     *  | Regex "+"                     %Plus
     *  | Regex "?"                     %Option
     *  | "(" Regex ")"                 %Bracket
     *  | "(" Regex Regex+ ")"          %Sequence
     *  | "(" Regex ("|" Regex)+ ")"    %Alternation
     *  | Identifier                    %Reference
     *  | CharClass                     %CharClass
     *  | String                        %String
     *  | Char                          %Char
     */
    private RegularExpression visitRegex(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
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

            // TODO: add an optimization step to flatten alternation
            case "Alternation": {
                List<RegularExpression> list = new ArrayList<>();
                list.add((RegularExpression) node.childAt(1).accept(this));
                list.addAll((Collection<? extends RegularExpression>) node.childAt(2).accept(this));
                return iguana.regex.Alt.from(list);
            }

            case "Reference":
                return iguana.regex.Reference.from(getIdentifier(node.childAt(0)).id);

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
                throw new RuntimeException("Unexpected label");
        }
    }

    /*
     * CharClass
     *   : '[' Range* ']'    #Chars
     *   | '[^' Range* ']'   #NotChars
     *   ;
     */
    private iguana.regex.Alt visitCharClass(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "Chars":
                return iguana.regex.Alt.from((List<RegularExpression>) node.childAt(1).accept(this));

            case "NotChars":
                return iguana.regex.Alt.not((List<RegularExpression>) node.childAt(1).accept(this));

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
        return new Identifier(node.getText());
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

    public static class Identifier {
        public final String id;

        public Identifier(String id) {
            this.id = id;
        }
    }
}
