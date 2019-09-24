package org.iguana.grammar.symbol;

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.util.serialization.JsonSerializer;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class HighLevelGrammar implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<HighLevelRule> rules;
    private final Start startSymbol;
    private final Symbol layout;

    private Grammar grammar;

    HighLevelGrammar(Builder builder) {
        this.rules = builder.rules;
        this.startSymbol = builder.startSymbol;
        this.layout = builder.layout;
    }

    public Start getStartSymbol() {
        return startSymbol;
    }

    public Symbol getLayout() {
        return layout;
    }

    public Grammar toGrammar() {
        if (grammar == null) {
            Grammar.Builder grammarBuilder = new Grammar.Builder();
            for (HighLevelRule highLevelRule : rules) {
                grammarBuilder.addRules(getRules(highLevelRule));
            }
            grammar = grammarBuilder.build();
        }

        return grammar;
    }

    public static HighLevelGrammar load(URI uri, String format) throws FileNotFoundException {
        return load(new File(uri), format);
    }

    public static HighLevelGrammar load(String path, String format) throws FileNotFoundException {
        return load(new File(path), format);
    }

    public static HighLevelGrammar load(File file) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        return load(fis, "binary");
    }

    public static HighLevelGrammar load(File file, String format) throws FileNotFoundException {
        return load(new FileInputStream(file), format);
    }

    public static HighLevelGrammar load(InputStream inputStream) {
        return load(inputStream, "binary");
    }

    public static HighLevelGrammar load(InputStream inputStream, String format) {
        HighLevelGrammar grammar;
        switch (format) {
            case "binary":
                try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(inputStream))) {
                    grammar = (HighLevelGrammar) in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "json":
                try {
                    grammar = JsonSerializer.deserialize(inputStream, HighLevelGrammar.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            default:
                throw new RuntimeException("Unsupported format exception");
        }
        return grammar;
    }

    public static class Builder {
        private List<HighLevelRule> rules = new ArrayList<>();
        private Start startSymbol;
        private Symbol layout;

        public Builder addHighLevelRule(HighLevelRule rule) {
            this.rules.add(rule);
            return this;
        }

        public Builder setStartSymbol(Start startSymbol) {
            this.startSymbol = startSymbol;
            return this;
        }

        public Builder setLayout(Symbol layout) {
            this.layout = layout;
            return this;
        }

        public HighLevelGrammar build() {
            return new HighLevelGrammar(this);
        }
    }

    private static List<Rule> getRules(HighLevelRule highLevelRule) {
        String name = highLevelRule.head;
        List<String> parameters = highLevelRule.parameters;
        List<Alternatives> alternativesList = highLevelRule.alternativesList;

        List<Rule> rules = new ArrayList<>();
        PrecedenceLevel level = PrecedenceLevel.getFirst();
        Nonterminal head = Nonterminal.withName(name);

        if (!parameters.isEmpty())
            head = head.copyBuilder().addParameters(parameters).build();

        ListIterator<Alternatives> altsIt = alternativesList.listIterator(alternativesList.size());
        while (altsIt.hasPrevious()) {
            Alternatives group = altsIt.previous();
            ListIterator<Alternative> altIt = group.alternatives.listIterator(group.alternatives.size());
            while (altIt.hasPrevious()) {
                Alternative alternative = altIt.previous();
                if (alternative.rest != null) { // Associativity group
                    AssociativityGroup assocGroup = new AssociativityGroup(alternative.associativity, level);
                    List<Seq> sequences = new ArrayList<>(Arrays.asList(alternative.first));
                    sequences.addAll(alternative.rest);
                    ListIterator<Seq> seqIt = sequences.listIterator(sequences.size());
                    while (seqIt.hasPrevious()) {
                        Seq sequence = seqIt.previous();
                        Rule rule = getRule(head, sequence.getSymbols(), sequence.associativity);
                        int precedence = assocGroup.getPrecedence(rule);
                        rule = rule.copyBuilder().setPrecedence(precedence).setPrecedenceLevel(level).setAssociativityGroup(assocGroup).build();
                        rules.add(rule);
                    }
                    assocGroup.done();
                    level.containsAssociativityGroup(assocGroup.getLhs(), assocGroup.getRhs());
                } else {
                    List<Symbol> symbols = new ArrayList<>();
                    if (alternative.first == null) { // Empty alternative
                        Rule rule = getRule(head, symbols, Associativity.UNDEFINED);
                        int precedence = level.getPrecedence(rule);
                        rule = rule.copyBuilder().setPrecedence(precedence).setPrecedenceLevel(level).build();
                        rules.add(rule);
                    } else {
                        symbols.add(alternative.first.first);
                        if (alternative.first.rest != null)
                            addAll(symbols, alternative.first.rest);
                        Rule rule = getRule(head, symbols, alternative.first.associativity);
                        int precedence = level.getPrecedence(rule);
                        rule = rule.copyBuilder().setPrecedence(precedence).setPrecedenceLevel(level).build();
                        rules.add(rule);
                    }
                }
            }
            level.setUndefinedIfNeeded();
            level = level.getNext();
        }
        level.done();
        Collections.reverse(rules);
        return rules;
    }

    private static Rule getRule(Nonterminal head, List<Symbol> symbols, Associativity associativity) {
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
}
