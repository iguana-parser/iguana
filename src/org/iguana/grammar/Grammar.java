package org.iguana.grammar;

import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.runtime.*;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.ResolveIdentifiers;
import org.iguana.iggy.IggyParserBootstrap;
import org.iguana.regex.*;
import org.iguana.regex.visitor.RegularExpressionVisitor;
import org.iguana.traversal.ISymbolVisitor;
import org.iguana.util.serialization.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {

    private final List<Rule> rules;
    private final Map<String, RegularExpression> regularExpressions;
    private final Map<String, RegularExpression> literals;
    private final Start startSymbol;
    private final Symbol layout;
    private final Map<String, Expression> globals;
    private final String name;

    private final Map<String, Set<String>> leftEnds = new HashMap<>();
    private final Map<String, Set<String>> rightEnds = new HashMap<>();
    private final Set<String> ebnfs = new HashSet<>();

    private RuntimeGrammar grammar;

    Grammar(Builder builder) {
        this.rules = builder.rules;
        this.regularExpressions = builder.regularExpressions;
        this.literals = builder.literals;
        this.startSymbol = builder.startSymbol;
        this.layout = builder.layout;
        this.globals = builder.globals;
        this.name = builder.name;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Map<String, RegularExpression> getRegularExpressions() {
        return regularExpressions;
    }

    public Map<String, RegularExpression> getLiterals() {
        return literals;
    }

    public Start getStartSymbol() {
        return startSymbol;
    }

    public Symbol getLayout() {
        return layout;
    }

    public Map<String, Expression> getGlobals() {
        return globals;
    }

    public String getName() {
        return name;
    }

    public RuntimeGrammar toRuntimeGrammar() {
        if (grammar == null) {
            computeEnds();

            // TODO: make these transformations explicit
            Map<String, RegularExpression> regularExpressions = InlineReferences.inline(this.regularExpressions);
            Set<String> nonterminals = rules.stream().map(r -> r.getHead().getName()).collect(Collectors.toSet());
            ResolveIdentifiers resolveIdentifiers = new ResolveIdentifiers(nonterminals, regularExpressions);

            RuntimeGrammar.Builder grammarBuilder = new RuntimeGrammar.Builder();
            for (Rule rule : rules) {
                if (rule.getHead().toString().equals("$default$")) continue;
                grammarBuilder.addRules(getRules(rule, resolveIdentifiers));
            }
            grammarBuilder.setStartSymbol(startSymbol);

            // Resolve the layout symbol
            Symbol newLayout = null;
            if (layout != null) {
                newLayout = layout.accept(resolveIdentifiers);
                if (newLayout instanceof Terminal) {
                    newLayout = ((Terminal) newLayout).copy().setNodeType(TerminalNodeType.Layout).build();
                } else if (newLayout instanceof Nonterminal) {
                    newLayout = ((Nonterminal) newLayout).copy().setNodeType(NonterminalNodeType.Layout).build();
                } else {
                    throw new RuntimeException("Layout can only be an instance of a terminal or nonterminal, but was " + newLayout.getClass().getSimpleName());
                }
            }

            grammarBuilder.setLayout(newLayout);
            grammarBuilder.setGlobals(globals);
            grammarBuilder.setRegularExpressions(regularExpressions);
            grammarBuilder.setLiterals(literals);

            Map<String, Set<String>> ebnfLefts = new HashMap<>();
            Map<String, Set<String>> ebnfRights = new HashMap<>();

            for (String ebnf : ebnfs) {
                Set<String> set = leftEnds.get(ebnf);
                if (set != null)
                    ebnfLefts.put(ebnf, set);

                set = rightEnds.get(ebnf);
                if (set != null)
                    ebnfRights.put(ebnf, set);
            }

            grammarBuilder.addEBNFl(ebnfLefts);
            grammarBuilder.addEBNFr(ebnfRights);
            grammar = grammarBuilder.build();
        }

        return grammar;
    }

    private void computeEnds() {
        for (Rule rule : rules) {
            if (rule.getHead().toString().equals("$default$")) continue;
            for (PriorityLevel priorityLevel : rule.getPriorityLevels()) {
                for (Alternative alternative : priorityLevel.getAlternatives()) {
                    for (Sequence seq : alternative.seqs()) {
                        computeEnds(rule.getHead(), seq.getSymbols());
                    }
                }
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (String head : leftEnds.keySet()) {
                Set<String> ends = leftEnds.get(head);
                int size = ends.size();
                Set<String> delta = new HashSet<>();
                for (String end : ends) {
                    Set<String> lefts = leftEnds.get(end);
                    if (lefts != null) {
                        for (String left : lefts) {
                            if (!left.equals(head))
                                delta.add(left);
                        }
                    }
                }
                ends.addAll(delta);
                if (ends.size() != size)
                    changed = true;
            }
        }

        changed = true;
        while (changed) {
            changed = false;
            for (String head : rightEnds.keySet()) {
                Set<String> ends = rightEnds.get(head);
                int size = ends.size();
                Set<String> delta = new HashSet<>();
                for (String end : ends) {
                    Set<String> rights = rightEnds.get(end);
                    if (rights != null) {
                        for (String right : rights) {
                            if (!right.equals(head))
                                delta.add(right);
                        }
                    }
                }
                ends.addAll(delta);
                if (ends.size() != size)
                    changed = true;
            }
        }
    }

    public static Grammar fromIggyGrammar(String content) {
        return IggyParserBootstrap.fromGrammar(content);
    }

    public static Grammar fromJsonFile(String path) {
        try {
            return JsonSerializer.deserialize(Files.newInputStream(new File(path).toPath()), Grammar.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public int hashCode() {
        return Objects.hash(rules, layout, startSymbol);
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
        private final List<Rule> rules = new ArrayList<>();
        private final Map<String, RegularExpression> regularExpressions = new LinkedHashMap<>();
        public final Map<String, RegularExpression> literals = new LinkedHashMap<>();
        public String name;
        private Start startSymbol;
        private Symbol layout;
        private final Map<String, Expression> globals = new HashMap<>();

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

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder addRegularExpression(String name, RegularExpression regularExpression) {
            regularExpressions.put(name, regularExpression);
            return this;
        }

        public Builder addLiteral(String name, RegularExpression regularExpression) {
            literals.put(name, regularExpression);
            return this;
        }

        // TODO: differentiate between global variables and top-level variables
        public Builder addGlobal(String key, Expression value) {
            this.globals.put(key, value);
            return this;
        }

        public Grammar build() {
            return new Grammar(this);
        }
    }

    private List<RuntimeRule> getRules(Rule highLevelRule, ResolveIdentifiers resolveIdentifiers) {
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
                        RuntimeRule rule = getRule(head, sequence.getSymbols(), sequence.associativity, sequence.label, resolveIdentifiers, highLevelRule.getLayoutStrategy());
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
                        RuntimeRule rule = getRule(head, symbols, Associativity.UNDEFINED, label, resolveIdentifiers, highLevelRule.getLayoutStrategy());
                        int precedence = level.getPrecedence(rule);
                        rule = rule.copyBuilder().setPrecedence(precedence).setPrecedenceLevel(level).build();
                        rules.add(rule);
                    } else {
                        symbols.add(alternative.first().first());
                        if (alternative.first().rest() != null)
                            addAll(symbols, alternative.first().rest());
                        RuntimeRule rule = getRule(head, symbols, alternative.first().associativity, alternative.first().label, resolveIdentifiers, highLevelRule.getLayoutStrategy());
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

    private RuntimeRule getRule(Nonterminal head, List<Symbol> body, Associativity associativity, String label, ResolveIdentifiers resolveIdentifiers, LayoutStrategy layoutStrategy) {
        boolean isLeft = body.size() != 0 && body.get(0).accept(new IsRecursive(head, Recursion.LEFT_REC, leftEnds, ebnfs, layout));
        boolean isRight = body.size() != 0 && body.get(body.size() - 1).accept(new IsRecursive(head, Recursion.RIGHT_REC, leftEnds, ebnfs, layout));

        IsRecursive visitor = new IsRecursive(head, Recursion.iLEFT_REC, leftEnds, ebnfs, layout);

        boolean isiLeft = body.size() != 0 && body.get(0).accept(visitor);
        String leftEnd = visitor.getEnd();

        visitor = new IsRecursive(head, Recursion.iRIGHT_REC, rightEnds, ebnfs, layout);
        boolean isiRight = body.size() != 0 && body.get(body.size() - 1).accept(visitor);
        String rightEnd = visitor.getEnd();

        Recursion recursion = Recursion.NON_REC;
        Recursion irecursion = Recursion.NON_REC;
        int precedence = -1;

        if (isLeft && isRight)
            recursion = Recursion.LEFT_RIGHT_REC;
        else if (isLeft)
            recursion = Recursion.LEFT_REC;
        else if (isRight)
            recursion = Recursion.RIGHT_REC;

        if (isiLeft && isiRight)
            irecursion = Recursion.iLEFT_RIGHT_REC;
        else if (isiLeft)
            irecursion = Recursion.iLEFT_REC;
        else if (isiRight)
            irecursion = Recursion.iRIGHT_REC;

        if (recursion == Recursion.NON_REC && irecursion == Recursion.NON_REC)
            associativity = Associativity.UNDEFINED;

        // Mixed cases
        boolean isPrefixOrCanBePrefix = (irecursion != Recursion.iLEFT_REC && recursion == Recursion.RIGHT_REC)
            || (recursion != Recursion.LEFT_REC && irecursion == Recursion.iRIGHT_REC);
        boolean isPostfixOrCanBePostfix = (recursion == Recursion.LEFT_REC && irecursion != Recursion.iRIGHT_REC)
            || (irecursion == Recursion.iLEFT_REC && recursion != Recursion.RIGHT_REC);

        if ((isPrefixOrCanBePrefix || isPostfixOrCanBePostfix) && associativity != Associativity.NON_ASSOC)
            associativity = Associativity.UNDEFINED;

        if (associativity == null) {
            associativity = Associativity.UNDEFINED;
        }

        List<Symbol> newSymbols = body.stream().map(symbol -> symbol.accept(resolveIdentifiers)).collect(Collectors.toList());

        return RuntimeRule.withHead(head)
            .addSymbols(newSymbols)
            .setRecursion(recursion)
            .setiRecursion(irecursion)
            .setLeftEnd(leftEnd)
            .setRightEnd(rightEnd)
            .setLeftEnds(leftEnds.get(head.getName()))
            .setRightEnds(rightEnds.get(head.getName()))
            .setAssociativity(associativity)
            .setPrecedence(precedence)
            .setLayoutStrategy(layoutStrategy)
            .setLabel(label)
            .build();
    }

    private void computeEnds(Nonterminal head, List<Symbol> symbols) {
        if (symbols.size() >= 1) {
            Symbol first = symbols.get(0);
            Symbol last = symbols.get(symbols.size() - 1);

            IsRecursive isLeft = new IsRecursive(head, Recursion.LEFT_REC, ebnfs);

            if(!first.accept(isLeft) && !isLeft.getEnd().isEmpty()) {
                Set<String> ends = leftEnds.get(head.getName());
                if (ends == null) {
                    ends = new HashSet<>();
                    leftEnds.put(head.getName(), ends);
                }
                ends.add(isLeft.getEnd());
                if (!isLeft.ends.isEmpty()) // EBNF related
                    leftEnds.putAll(isLeft.ends);
            }

            IsRecursive isRight = new IsRecursive(head, Recursion.RIGHT_REC, ebnfs);

            if(!last.accept(isRight) && !isRight.getEnd().isEmpty()) {
                Set<String> ends = rightEnds.get(head.getName());
                if (ends == null) {
                    ends = new HashSet<>();
                    rightEnds.put(head.getName(), ends);
                }
                ends.add(isRight.getEnd());
                if (!isRight.ends.isEmpty()) // EBNF related
                    rightEnds.putAll(isRight.ends);
            }
        }
    }

    private static class IsRecursive implements ISymbolVisitor<Boolean>, RegularExpressionVisitor<Boolean> {

        private final Recursion recursion;
        private final Nonterminal head;
        private final Symbol layout;

        private final Map<String, Set<String>> ends;
        private final Set<String> ebnfs;

        private String end = "";

        public IsRecursive(Nonterminal head, Recursion recursion, Set<String> ebnfs) {
            this(head, recursion, new HashMap<>(), ebnfs, null);
        }

        public IsRecursive(Nonterminal head, Recursion recursion, Map<String, Set<String>> ends, Set<String> ebnfs, Symbol layout) {
            this.recursion = recursion;
            this.head = head;
            this.layout = layout;
            this.ends = ends;
            this.ebnfs = ebnfs;
        }

        private String getEnd() {
            return end;
        }

        @Override
        public Boolean visit(Align symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Boolean visit(Block symbol) {
            Symbol[] symbols = symbol.getSymbols();
            if (recursion == Recursion.LEFT_REC || recursion == Recursion.iLEFT_REC)
                return symbols[0].accept(this);
            else
                return symbols[symbols.length - 1].accept(this);
        }

        @Override
        public Boolean visit(Char symbol) {
            return false;
        }

        @Override
        public Boolean visit(CharRange symbol) {
            return false;
        }

        @Override
        public Boolean visit(Code symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Boolean visit(Conditional symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Boolean visit(EOF symbol) {
            return false;
        }

        @Override
        public Boolean visit(Epsilon symbol) {
            return false;
        }

        @Override
        public Boolean visit(IfThen symbol) {
            return symbol.getThenPart().accept(this);
        }

        @Override
        public Boolean visit(IfThenElse symbol) {
            return symbol.getThenPart().accept(this)
                || symbol.getElsePart().accept(this);
        }

        @Override
        public Boolean visit(Ignore symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Boolean visit(Identifier symbol) {
            end = symbol.getName();

            if (recursion == Recursion.LEFT_REC || recursion == Recursion.RIGHT_REC) {
                if (symbol.getName().equals(head.getName())) {
                    return true;
                }
            } else {
                Set<String> set = ends.get(symbol.getName());
                if (set != null && set.contains(head.getName()))
                    return true;
            }

            return false;
        }

            @Override
        public Boolean visit(Nonterminal symbol) {

            end = symbol.getName();

            if (recursion == Recursion.LEFT_REC || recursion == Recursion.RIGHT_REC) {
                if (symbol.getName().equals(head.getName())
                    && ((head.getParameters() == null && symbol.getArguments() == null)
                    || (head.getParameters().size() == symbol.getArguments().length)))
                    return true;

            } else {
                Set<String> set = ends.get(symbol.getName());
                if (set != null && set.contains(head.getName()))
                    return true;
            }

            return false;
        }

        @Override
        public Boolean visit(Offside symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Boolean visit(Terminal symbol) {
            end = "$" + head.getName();
            return false;
        }

        @Override
        public Boolean visit(While symbol) {
            return symbol.getBody().accept(this);
        }

        @Override
        public Boolean visit(Return symbol) {
            return false;
        }

        @Override
        public Boolean visit(org.iguana.grammar.symbol.Alt symbol) {
            System.out.println("Warning: indirect recursion isn't yet supported for (.|.).");
            return false;
        }

        @Override
        public Boolean visit(org.iguana.grammar.symbol.Opt symbol) {
            System.out.println("Warning: indirect recursion isn't yet supported for options.");
            return false;
        }

        @Override
        public Boolean visit(org.iguana.grammar.symbol.Plus symbol) {

            if (recursion == Recursion.LEFT_REC || recursion == Recursion.RIGHT_REC) {

                IsRecursive visitor = new IsRecursive(head, recursion, ebnfs);
                symbol.getSymbol().accept(visitor);

                String name = EBNFToBNF.getName(Nonterminal.withName(visitor.end), symbol.getSeparators(), null) + "+";
                end = name;

                ends.putAll(visitor.ends);
                ends.put(name, new HashSet<String>(Arrays.asList(visitor.end)));
                ebnfs.add(name);

                return false;
            } else {

                IsRecursive visitor = new IsRecursive(head, recursion, ends, ebnfs, null);
                symbol.getSymbol().accept(visitor);

                String name = EBNFToBNF.getName(Nonterminal.withName(visitor.end), symbol.getSeparators(), null) + "+";
                end = name;

                Set<String> set = ends.get(name);
                if (set != null && set.contains(head.getName()))
                    return true;
            }
            return false;
        }

        @Override
        public Boolean visit(org.iguana.grammar.symbol.Group symbol) {

            if (recursion == Recursion.LEFT_REC || recursion == Recursion.RIGHT_REC) {

                IsRecursive visitor = new IsRecursive(head, recursion, ebnfs);

                if (recursion == Recursion.LEFT_REC)
                    symbol.get(0).accept(visitor);
                else
                    symbol.getSymbols().get(symbol.getSymbols().size() - 1).accept(visitor);

                String name = symbol.getName();
                end = name;

                ends.putAll(visitor.ends);
                ends.put(name, new HashSet<String>(Arrays.asList(visitor.end)));
                ebnfs.add(name);

            } else {
                IsRecursive visitor = new IsRecursive(head, recursion, ends, ebnfs, null);

                if (recursion == Recursion.iLEFT_REC)
                    symbol.getSymbols().get(0).accept(visitor);
                else
                    symbol.getSymbols().get(symbol.getSymbols().size() - 1).accept(visitor);

                String name = symbol.getName();
                end = name;

                Set<String> set = ends.get(name);
                if (set != null && set.contains(head.getName()))
                    return true;
            }

            return false;
        }

        @Override
        public Boolean visit(org.iguana.grammar.symbol.Star symbol) {

            if (recursion == Recursion.LEFT_REC || recursion == Recursion.RIGHT_REC) { // TODO: not good, there should be also left and right ends

                IsRecursive visitor = new IsRecursive(head, recursion, ebnfs);
                symbol.getSymbol().accept(visitor);

                String base = EBNFToBNF.getName(Nonterminal.withName(visitor.end), symbol.getSeparators(), null);
                String name = base + "*";
                end = name;

                ends.putAll(visitor.ends);
                ends.put(name, new HashSet<String>(Arrays.asList(base + "+")));
                ends.put(base + "+", new HashSet<String>(Arrays.asList(visitor.end)));
                ebnfs.add(name);
                ebnfs.add(base + "+");
                return false;
            } else {

                IsRecursive visitor = new IsRecursive(head, recursion, ends, ebnfs, null);
                symbol.getSymbol().accept(visitor);

                String base = EBNFToBNF.getName(Nonterminal.withName(visitor.end), symbol.getSeparators(), null);
                String name = base + "*";
                end = name;

                Set<String> set = ends.get(name);
                if (set != null && set.contains(head.getName()))
                    return true;
            }
            return false;
        }

        @Override
        public Boolean visit(org.iguana.regex.Star s) {
            return false;
        }

        @Override
        public Boolean visit(org.iguana.regex.Plus p) {
            return false;
        }

        @Override
        public Boolean visit(org.iguana.regex.Opt o) {
            return false;
        }

        @Override
        public <E extends RegularExpression> Boolean visit(org.iguana.regex.Alt<E> alt) {
            return false;
        }

        @Override
        public <E extends RegularExpression> Boolean visit(org.iguana.regex.Seq<E> seq) {
            return false;
        }

        @Override
        public Boolean visit(Start start) {
            throw new IllegalStateException();
//            return start.getNonterminal().accept(this);
        }

        @Override
        public Boolean visit(Reference ref) {
            // TODO Auto-generated method stub
            return null;
        }

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
}
