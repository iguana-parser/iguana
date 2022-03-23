package org.iguana.grammar;

import iguana.regex.InlineReferences;
import iguana.regex.RegularExpression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.runtime.*;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.SymbolToSymbolVisitor;
import org.iguana.util.serialization.JsonSerializer;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Rule> rules;
    private final Map<String, RegularExpression> terminals;
    private final Start startSymbol;
    private final Symbol layout;

    private RuntimeGrammar grammar;

    Grammar(Builder builder) {
        this.rules = builder.rules;
        this.terminals = builder.terminals;
        this.startSymbol = builder.startSymbol;
        this.layout = builder.layout;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Map<String, RegularExpression> getTerminals() {
        return terminals;
    }

    public Start getStartSymbol() {
        return startSymbol;
    }

    public Symbol getLayout() {
        return layout;
    }

    public RuntimeGrammar toRuntimeGrammar() {
        if (grammar == null) {
            RuntimeGrammar.Builder grammarBuilder = new RuntimeGrammar.Builder();
            for (Rule rule : rules) {
                if (rule.getHead().toString().equals("$default$")) continue;
                grammarBuilder.addRules(getRules(rule));
            }
            grammarBuilder.setStartSymbol(startSymbol);
            grammarBuilder.setLayout(layout);
            grammarBuilder.setTerminals(InlineReferences.inline(terminals));
            grammar = grammarBuilder.build();
        }

        return grammar;
    }

    public static Grammar load(URI uri, String format) throws FileNotFoundException {
        return load(new File(uri), format);
    }

    public static Grammar load(String path, String format) throws FileNotFoundException {
        return load(new File(path), format);
    }

    public static Grammar load(File file) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        return load(fis, "binary");
    }

    public static Grammar load(File file, String format) throws FileNotFoundException {
        return load(new FileInputStream(file), format);
    }

    public static Grammar load(InputStream inputStream) {
        return load(inputStream, "binary");
    }

    public static Grammar load(InputStream inputStream, String format) {
        Grammar grammar;
        switch (format) {
            case "binary":
                try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(inputStream))) {
                    grammar = (Grammar) in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "json":
                try {
                    grammar = JsonSerializer.deserialize(inputStream, Grammar.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;

            default:
                throw new RuntimeException("Unsupported format exception");
        }
        return grammar;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Grammar)) return false;
        Grammar other = (Grammar) obj;
        return this.rules.equals(other.rules) && Objects.equals(this.layout, other.layout)
                && Objects.equals(this.startSymbol, other.startSymbol);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Rule rule : rules) {
            sb.append(rule).append("\n");
        }
        return sb.toString();
    }

    public static class Builder {
        private List<Rule> rules = new ArrayList<>();
        private Map<String, RegularExpression> terminals = new HashMap<>();
        private Start startSymbol;
        private Symbol layout;

        public Builder addRule(Rule rule) {
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

        public Builder addTerminal(String name, RegularExpression regularExpression) {
            terminals.put(name, regularExpression);
            return this;
        }

        public Builder addTerminals(Map<String, RegularExpression> terminals) {
            this.terminals.putAll(terminals);
            return this;
        }

        public Grammar build() {
            return new Grammar(this);
        }
    }

    private static List<RuntimeRule> getRules(Rule highLevelRule) {
        List<PriorityLevel> priorityLevels = highLevelRule.getPriorityLevels();

        List<RuntimeRule> rules = new ArrayList<>();
        PrecedenceLevel level = PrecedenceLevel.getFirst();
        Nonterminal head = highLevelRule.getHead();

        ListIterator<PriorityLevel> altsIt = priorityLevels.listIterator(priorityLevels.size());
        while (altsIt.hasPrevious()) {
            PriorityLevel group = altsIt.previous();
            ListIterator<Alternative> altIt = group.getAlternatives().listIterator(group.getAlternatives().size());
            while (altIt.hasPrevious()) {
                Alternative alternative = altIt.previous();
                if (alternative.rest() != null) { // Associativity group
                    AssociativityGroup assocGroup = new AssociativityGroup(alternative.associativity, level);
                    List<Sequence> sequences = new ArrayList<>(Arrays.asList(alternative.first()));
                    sequences.addAll(alternative.rest());
                    ListIterator<Sequence> seqIt = sequences.listIterator(sequences.size());
                    while (seqIt.hasPrevious()) {
                        Sequence sequence = seqIt.previous();
                        RuntimeRule rule = getRule(head, sequence.getSymbols(), sequence.associativity, sequence.label);
                        int precedence = assocGroup.getPrecedence(rule);
                        rule = rule.copyBuilder().setPrecedence(precedence).setPrecedenceLevel(level).setAssociativityGroup(assocGroup).build();
                        rules.add(rule);
                    }
                    assocGroup.done();
                    level.containsAssociativityGroup(assocGroup.getLhs(), assocGroup.getRhs());
                } else {
                    List<Symbol> symbols = new ArrayList<>();
                    if (alternative.first() == null || alternative.first().isEmpty()) { // Empty alternative
                        String label = alternative.first() == null ? null : alternative.first().label;
                        RuntimeRule rule = getRule(head, symbols, Associativity.UNDEFINED, label);
                        int precedence = level.getPrecedence(rule);
                        rule = rule.copyBuilder().setPrecedence(precedence).setPrecedenceLevel(level).build();
                        rules.add(rule);
                    } else {
                        symbols.add(alternative.first().first());
                        if (alternative.first().rest() != null)
                            addAll(symbols, alternative.first().rest());
                        RuntimeRule rule = getRule(head, symbols, alternative.first().associativity, alternative.first().label);
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

    private static RuntimeRule getRule(Nonterminal head, List<Symbol> symbols, Associativity associativity, String label) {
        symbols = convertToCode(symbols.stream().map(s -> s.accept(new MyVisitor())).collect(Collectors.toList()));
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

        RuntimeRule.Builder builder = RuntimeRule.withHead(head)
                .addSymbols(symbols)
                .setRecursion(recursion)
                .setLabel(label)
                .setAssociativity(associativity);

        return builder.build();
    }

    private static void addAll(List<Symbol> symbols, List<Symbol> rest) {
        int i = 0;
        while (i < rest.size()) {
            Symbol current = rest.get(i);
            if (i < rest.size() - 1 && rest.get(i + 1) instanceof CodeHolder) {
                CodeHolder holder = (CodeHolder) rest.get(i + 1);
                symbols.add(Code.code(current, holder.statement));
                i += 2;
            } else {
                symbols.add(current);
                i += 1;
            }
        }
    }

    private static List<Symbol> convertToCode(List<Symbol> symbols) {
        List<Symbol> result = new ArrayList<>();
        int i = 0;
        while (i < symbols.size()) {
            Symbol current = symbols.get(i);
            int j = i;
            while (j < symbols.size() - 1 && symbols.get(j + 1) instanceof CodeHolder) {
                j++;
            }
            // [i + 1 ... j] are statements.
            if (j > i) {
                List<Statement> statements = new ArrayList<>(j - i);
                for (int k = i + 1; k <= j; k++) {
                    statements.add(((CodeHolder) symbols.get(k)).statement);
                }
                result.add(Code.code(current, statements.toArray(new Statement[] {})));
                i = j + 1;
            } else {
                result.add(current);
                i += 1;
            }
        }
        return result;
    }

    static class MyVisitor implements SymbolToSymbolVisitor {
        @Override
        public Symbol visit(Plus symbol) {
            Symbol newSymbol = SymbolToSymbolVisitor.super.visit(symbol);
            return newSymbol.copy().setChildren(convertToCode(newSymbol.getChildren())).build();
        }

        @Override
        public Symbol visit(Group symbol) {
            Symbol newSymbol = SymbolToSymbolVisitor.super.visit(symbol);
            return newSymbol.copy().setChildren(convertToCode(newSymbol.getChildren())).build();
        }

        @Override
        public Symbol visit(Star symbol) {
            Symbol newSymbol = SymbolToSymbolVisitor.super.visit(symbol);
            return newSymbol.copy().setChildren(convertToCode(newSymbol.getChildren())).build();
        }
    }
}