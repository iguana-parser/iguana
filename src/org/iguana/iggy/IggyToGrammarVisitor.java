package org.iguana.iggy;

import iguana.regex.Char;
import iguana.regex.CharRange;
import iguana.regex.RegularExpression;
import iguana.regex.Seq;
import org.eclipse.imp.pdb.facts.util.ImmutableSet;
import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.*;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.parsetree.ParseTreeVisitor;
import org.iguana.traversal.ISymbolVisitor;

import java.util.*;
import java.util.stream.Collectors;

public class IggyToGrammarVisitor implements ParseTreeVisitor {

    private Map<String, Terminal> terminalsMap = new HashMap<>();

    @Override
    public Object visitNonterminalNode(NonterminalNode node) {
        switch (node.getName()) {
            case "Definition":
                return visitDefinition(node);

            case "Rule":
                return visitRule(node);

            case "Parameters":
                return visitParameters(node);

            case "Alternatives":
                return visitAlternatives(node);

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
        }

        return visitChildren(node);
    }

    /*
     * Definition: Rule+;
     */
    private Grammar visitDefinition(NonterminalNode node) {
        Grammar.Builder builder = Grammar.builder();
        List<Rule> rules = new ArrayList<>();
        // Each rule in the textual syntax may represent multiple grammar rules in our symbol definition,
        // as we don't natively support alternatives.
        for (Object obj : (List<?>) node.childAt(0).accept(this)) {
            if (obj instanceof List<?>) {
                rules.addAll((List<Rule>) obj);
            } else {
                rules.add((Rule) obj);
            }
        }
        builder.addRules(rules);
        return builder.build();
    }

    /*
     * Rule : Identifier Parameters? ":" Body                   %Syntax
     *      | "layout"? "terminal" Identifier ":" RegexBody     %Lexical
     *      ;
     */
    private List<Rule> visitRule(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "Syntax":
                Identifier nonterminalName = getIdentifier(node.getChildWithName("Identifier"));
                List<Identifier> parameters = (List<Identifier>) node.childAt(1).accept(this);
                if (parameters == null) parameters = Collections.emptyList();
                List<Alternatives> body = (List<Alternatives>) node.getChildWithName("Body").accept(this);
                if (body.isEmpty()) {
                    Rule rule = Rule.withHead(Nonterminal.withName(nonterminalName.id))
                            .setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
                            .build();
                    return Arrays.asList(rule);
                }
                return getRules(nonterminalName, parameters, body);

            case "Lexical":
                RegularExpression regex = (RegularExpression) node.getChildWithName("RegexBody").accept(this);
                Terminal terminal = Terminal.builder(regex).build();
                Identifier name = getIdentifier(node.getChildWithName("Identifier"));
                terminalsMap.put(name.id, terminal);
                return Collections.emptyList();

            default:
                throw new RuntimeException("Unexpected label");
        }
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
    private Alternatives visitAlternatives(NonterminalNode node) {
        return new Alternatives((List<Alternative>) node.childAt(0).accept(this));
    }

    /*
     * Alternative: Sequence                                             #Sequence
     *            | Associativity "(" Sequence ("|" Sequence)+ ")"       #Assoc
     */
    private Alternative visitAlternative(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "Sequence":
                return new Alternative((Sequence) node.childAt(0).accept(this));

            case "Assoc":
                Associativity associativity = getAssociativity(node.childAt(0).childAt(0));
                Sequence sequence = (Sequence) node.childAt(2).accept(this);
                List<Sequence> rest = (List<Sequence>) node.childAt(3).accept(this);
                return new Alternative(sequence, rest, associativity);

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
     *   | "(" Symbols ("|" Symbols)+ ")"   %Alternation
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
     *   ;
     */
    private Symbol visitSymbol(NonterminalNode node) {
        String label = node.getGrammarDefinition().getLabel();

        switch (label) {
            case "Call": {
                Expression[] expressions = ((List<Expression>) node.childAt(0).accept(this)).toArray(new Expression[]{});
                return Nonterminal.builder(getIdentifier(node.childAt(0)).id).apply(expressions).build();
            }

            case "Offside":
                return Offside.offside((Symbol) node.childAt(0).accept(this));

            case "Star":
                return Star.from((Symbol) node.childAt(0).accept(this));

            case "Plus":
                return Plus.from((Plus) node.childAt(0).accept(this));

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
                return symbol.copyBuilder().addPreCondition(RegularExpressionCondition.follow(regex)).build();
            }

            case "NotFollow": {
                Symbol symbol = (Symbol) node.childAt(0).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
                return symbol.copyBuilder().addPreCondition(RegularExpressionCondition.notFollow(regex)).build();
            }

            case "Exclude": {
                Symbol symbol = (Symbol) node.childAt(0).accept(this);
                RegularExpression regex = (RegularExpression) node.childAt(2).accept(this);
                return symbol.copyBuilder().addPreCondition(RegularExpressionCondition.notMatch(regex)).build();
            }

            case "Except": {
                Nonterminal symbol = (Nonterminal) node.childAt(0).accept(this);
                return symbol.copyBuilder().addExcept(getIdentifier(node.childAt(2)).id).build();
            }

            case "Nont":
                return Nonterminal.withName(getIdentifier(node).id);

            case "String":
            case "Character":
                return Terminal.from(getCharsRegex(node.getText()));

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

            // String: '"' Character* '"'
            case "String":
                return getCharsRegex(node.childAt(1).getText());

            // Char = '\'' Character* '\''
            case "Char": {
                String s = node.childAt(1).getText();
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

    private static List<Rule> getRules(Identifier name, List<Identifier> parameters, List<Alternatives> body) {
        List<Rule> rules = new ArrayList<>();
        PrecedenceLevel level = PrecedenceLevel.getFirst();
        Nonterminal head = Nonterminal.withName(name.id);

        if (!parameters.isEmpty())
            head = head.copyBuilder().addParameters(parameters.stream().map(p -> p.id).collect(Collectors.toList())).build();

        ListIterator<Alternatives> altsIt = body.listIterator(body.size());
        while (altsIt.hasPrevious()) {
            Alternatives group = altsIt.previous();
            ListIterator<Alternative> altIt = group.alternatives.listIterator(group.alternatives.size());
            while (altIt.hasPrevious()) {
                Alternative alternative = altIt.previous();
                if (alternative.rest != null) { // Associativity group
                    AssociativityGroup assocGroup = new AssociativityGroup(alternative.associativity, level);
                    List<Sequence> sequences = new ArrayList<>(Arrays.asList(alternative.first));
                    sequences.addAll(alternative.rest);
                    ListIterator<Sequence> seqIt = sequences.listIterator(sequences.size());
                    while (seqIt.hasPrevious()) {
                        Sequence sequence = seqIt.previous();
                        Rule rule = getRule(head, sequence.getSymbols(), sequence.associativity, sequence.label);
                        int precedence = assocGroup.getPrecedence(rule);
                        rule = rule.copyBuilder().setPrecedence(precedence).setPrecedenceLevel(level).setAssociativityGroup(assocGroup).build();
                        rules.add(rule);
                    }
                    assocGroup.done();
                    level.containsAssociativityGroup(assocGroup.getLhs(), assocGroup.getRhs());
                } else {
                    List<Symbol> symbols = new ArrayList<>();
                    symbols.add(alternative.first.first);
                    if (alternative.first.rest != null)
                        addAll(symbols, alternative.first.rest);
                    Rule rule = getRule(head, symbols, alternative.first.associativity, alternative.first.label);
                    int precedence = level.getPrecedence(rule);
                    rule = rule.copyBuilder().setPrecedence(precedence).setPrecedenceLevel(level).build();
                    rules.add(rule);
                }
            }
            level.setUndefinedIfNeeded();
            level = level.getNext();
        }
        level.done();
        Collections.reverse(rules);
        return rules;
    }

    private static void addAll(List<Symbol> symbols, List<Symbol> rest) {
        int i = 0;
        List<Condition> preConditions = new ArrayList<>();
        if (symbols.size() == 1 && symbols.get(0) instanceof CodeHolder) {
            CodeHolder holder = (CodeHolder) symbols.get(0);
            if (holder.expressions != null) {
                symbols.remove(0);
                preConditions = holder.expressions.stream().map(DataDependentCondition::predicate).collect(Collectors.toList());
                if (rest.isEmpty()) {
                    symbols.add(Terminal.epsilon().copyBuilder().addPreConditions(preConditions).build());
                    return;
                }
            }
        }

        for (Symbol symbol : rest) {
            if (symbol instanceof CodeHolder) {
                CodeHolder holder = (CodeHolder) symbol;
                if (holder.expressions != null) {
                    if (i != rest.size() - 1) {
                        for (Expression e : holder.expressions)
                            preConditions.add(DataDependentCondition.predicate(e));
                    } else {
                        Symbol last = symbols.remove(symbols.size() - 1);
                        symbols.add(last.copyBuilder().addPostConditions(holder.expressions.stream()
                                .map(DataDependentCondition::predicate).collect(Collectors.toList())).build());
                    }
                } else if (holder.statements != null) {
                    Symbol last = symbols.remove(symbols.size() - 1);
                    symbols.add(Code.code(last, holder.statements.toArray(new Statement[0])));
                }
            } else {
                if (preConditions.isEmpty())
                    symbols.add(symbol);
                else {
                    symbols.add(symbol.copyBuilder().addPreConditions(preConditions).build());
                    preConditions = new ArrayList<>();
                }
            }
            i++;
        }
    }

    private static class Alternatives {
        private final List<Alternative> alternatives;

        Alternatives(List<Alternative> alternatives) {
            this.alternatives = alternatives;
        }
    }

    private static Rule getRule(Nonterminal head, List<Symbol> symbols, Associativity associativity, String label) {
        // TODO: The first and the last symbol should be visited!
        boolean isLeft = !symbols.isEmpty() && symbols.get(0).getName().equals(head.getName());
        boolean isRight = !symbols.isEmpty() && symbols.get(symbols.size() - 1).getName().equals(head.getName());

        if (associativity == null)
            associativity = Associativity.UNDEFINED;

        Recursion recursion = Recursion.NON_REC;

        if (isLeft && isRight)
            recursion = Recursion.LEFT_RIGHT_REC;
        else if (isLeft)
            recursion = Recursion.LEFT_REC;
        else if (isRight)
            recursion = Recursion.RIGHT_REC;

        if (recursion == Recursion.NON_REC)
            associativity = Associativity.UNDEFINED;

        Rule.Builder builder = Rule.withHead(head)
                .addSymbols(symbols)
                .setRecursion(recursion)
                .setAssociativity(associativity);

        return builder.build();
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
        return Seq.from(getChars(s.substring(1, s.length() - 1)));
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

    private static class Alternative {
        public final Sequence first;
        public final List<Sequence> rest;
        public final Associativity associativity;

        public Alternative(Sequence sequence) {
            this.first = sequence;
            this.rest = null;
            this.associativity = null;
        }

        public Alternative(Sequence sequence, List<Sequence> sequences, Associativity associativity) {
            this.first = sequence;
            this.rest = sequences;
            this.associativity = associativity;
        }
    }

    private static class Sequence {
        public final Symbol first;
        public final List<Symbol> rest;
        public final Associativity associativity;
        public final String label;

        public Sequence(Symbol symbol, List<Symbol> rest, Associativity associativity, String label) {
            this.first = symbol;
            this.rest = rest;
            this.associativity = associativity;
            this.label = label;
        }

        public List<Symbol> getSymbols() {
            List<Symbol> symbols = new ArrayList<>();
            symbols.add(first);
            if (rest != null) {
                symbols.addAll(rest);
            }
            return symbols;
        }
    }

    private static class CodeHolder implements org.iguana.grammar.symbol.Symbol {

        public final List<Statement> statements;
        public final List<Expression> expressions;

        public CodeHolder(List<Statement> statements, List<Expression> expressions) {
            this.statements = statements;
            this.expressions = expressions;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Set<Condition> getPreConditions() {
            return null;
        }

        @Override
        public Set<Condition> getPostConditions() {
            return null;
        }

        @Override
        public String getLabel() {
            return null;
        }

        @Override
        public SymbolBuilder<? extends org.iguana.grammar.symbol.Symbol> copyBuilder() {
            return null;
        }

        @Override
        public String toString(int j) {
            return null;
        }

        @Override
        public <T> T accept(ISymbolVisitor<T> visitor) {
            return null;
        }

        @Override
        public ImmutableSet<String> getEnv() {
            return null;
        }

        @Override
        public void setEnv(ImmutableSet<String> env) {
        }

        @Override
        public void setEmpty() {
        }

    }

    public static class Identifier {
        public final String id;

        public Identifier(String id) {
            this.id = id;
        }
    }
}
