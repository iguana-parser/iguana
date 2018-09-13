/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.grammar;

import iguana.regex.CharRange;
import iguana.regex.matcher.DFAMatcherFactory;
import iguana.regex.matcher.MatcherFactory;
import iguana.utils.collections.rangemap.RangeMap;
import iguana.utils.collections.rangemap.RangeMapBuilder;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.condition.ConditionsFactory;
import org.iguana.grammar.exception.IncorrectNumberOfArgumentsException;
import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.slot.*;
import org.iguana.grammar.slot.EpsilonTransition.Type;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.slot.lookahead.RangeTreeFollowTest;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.VarToInt;
import org.iguana.util.Configuration;
import org.iguana.util.Configuration.EnvironmentImpl;

import java.io.Serializable;
import java.util.*;

public class GrammarGraph implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<Nonterminal, NonterminalGrammarSlot> nonterminalsMap;

    private Map<Terminal, TerminalGrammarSlot> terminalsMap;

    private List<GrammarSlot> slots;

    private FirstFollowSets firstFollow;

    private Grammar grammar;

    private Configuration config;

    private final Map<Integer, Map<String, Integer>> mapping;

    private Map<String, Integer> current;

    private static MatcherFactory matcherFactory = new DFAMatcherFactory();

    public static final TerminalGrammarSlot epsilonSlot = new TerminalGrammarSlot(Terminal.epsilon(), matcherFactory, ConditionsFactory.DEFAULT, ConditionsFactory.DEFAULT);;

    public static GrammarGraph from(Grammar grammar) {
        return from(grammar, Configuration.load());
    }

    public static GrammarGraph from(Grammar grammar, Configuration config) {
        GrammarGraph grammarGraph = new GrammarGraph(grammar, config);
        grammarGraph.convert();
        return grammarGraph;
    }

    private void convert() {
        this.firstFollow = new FirstFollowSets(this.grammar);

        terminalsMap.put(Terminal.epsilon(), epsilonSlot);

        add(epsilonSlot);

        Set<Nonterminal> nonterminals = this.grammar.getNonterminals();
        nonterminals.forEach(this::getNonterminalSlot);

        int i = 0;
        for (Rule r : this.grammar.getRules()) {
            current = mapping.get(i);
            convert(r);
            i++;
        }

        nonterminals.forEach(this::setFirstFollowTests);
    }

    private GrammarGraph(Grammar grammar, Configuration config) {
        if (config.getEnvImpl() == EnvironmentImpl.ARRAY || config.getEnvImpl() == EnvironmentImpl.INT_ARRAY) {
            VarToInt transformer = new VarToInt();
            this.grammar = transformer.transform(grammar);
            this.mapping = transformer.getMapping();
        } else {
            this.grammar = grammar;
            this.mapping = new HashMap<>();
        }

        this.config = config;
        this.nonterminalsMap = new LinkedHashMap<>();
        this.terminalsMap = new LinkedHashMap<>();
        this.slots = new ArrayList<>();
    }

    public NonterminalGrammarSlot getHead(Nonterminal start) {
        return nonterminalsMap.get(start);
    }

    public Collection<NonterminalGrammarSlot> getNonterminalGrammarSlots() {
        return nonterminalsMap.values();
    }

    public Collection<TerminalGrammarSlot> getTerminalGrammarSlots() {
        return terminalsMap.values();
    }

    private void convert(Rule rule) {
        Nonterminal nonterminal = rule.getHead();
        NonterminalGrammarSlot nonterminalSlot = getNonterminalSlot(nonterminal);
        addRule(nonterminalSlot, rule);
    }

    private void setFirstFollowTests(Nonterminal nonterminal) {
        NonterminalGrammarSlot nonterminalSlot = getNonterminalSlot(nonterminal);
        nonterminalSlot.setLookAheadTest(getLookAheadTest(nonterminal, nonterminalSlot));
        nonterminalSlot.setFollowTest(getFollowTest(nonterminal));
    }

    private RangeMap<BodyGrammarSlot> getLookAheadTest(Nonterminal nonterminal, NonterminalGrammarSlot nonterminalSlot) {
        if (config.getLookAheadCount() == 0)
            return i -> nonterminalSlot.getFirstSlots();

        List<Rule> alternatives = grammar.getAlternatives(nonterminal);

        RangeMapBuilder<BodyGrammarSlot> builder = new RangeMapBuilder<>();

        for (int i = 0; i < alternatives.size(); i++) {
            Rule rule = alternatives.get(i);
            BodyGrammarSlot firstSlot = nonterminalSlot.getFirstSlots().get(i);
            Set<CharRange> set = firstFollow.getPredictionSet(rule, 0);
            set.forEach(cr -> builder.put(cr, firstSlot));
        }

        return builder.buildRangeMap();
    }

    private FollowTest getFollowTest(Nonterminal nonterminal) {
        if (config.getLookAheadCount() == 0)
            return FollowTest.DEFAULT;

        return new RangeTreeFollowTest(firstFollow.getFollowSet(nonterminal));
    }

    private FollowTest getFollowTest(Rule rule, int i) {
        if (config.getLookAheadCount() == 0)
            return FollowTest.DEFAULT;

        return new RangeTreeFollowTest(firstFollow.getPredictionSet(rule, i));
    }

    private void addRule(NonterminalGrammarSlot head, Rule rule) {
        BodyGrammarSlot firstSlot = getFirstGrammarSlot(rule, head);
        head.addFirstSlot(firstSlot);

        GrammarGraphSymbolVisitor rule2graph = new GrammarGraphSymbolVisitor(head, rule, firstSlot);

        while (rule2graph.hasNext())
            rule2graph.nextSymbol();
    }

    private class GrammarGraphSymbolVisitor extends AbstractGrammarGraphSymbolVisitor<Void> {

        private final NonterminalGrammarSlot head;
        private final Rule rule;

        private BodyGrammarSlot currentSlot;
        private int i = 0;

        private int j = -1;

        GrammarGraphSymbolVisitor(NonterminalGrammarSlot head, Rule rule, BodyGrammarSlot currentSlot) {
            this.head = head;
            this.rule = rule;
            this.currentSlot = currentSlot;
        }

        boolean hasNext() {
            return i < rule.size();
        }

        void nextSymbol() {
            j = -1;
            visitSymbol(rule.symbolAt(i));
            i++;
        }

        public Void visit(Nonterminal symbol) {

            NonterminalGrammarSlot nonterminalSlot = getNonterminalSlot(symbol);

            BodyGrammarSlot slot;
            if (i == rule.size() - 1 && j == -1)
                slot = getEndSlot(rule, i + 1, rule.getPosition(i + 1), head, symbol.getLabel(), symbol.getVariable(), symbol.getState());
            else
                slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), symbol.getLabel(), symbol.getVariable(), symbol.getState());

            Expression[] arguments = symbol.getArguments();

            validateNumberOfArguments(nonterminalSlot.getNonterminal(), arguments);

            Set<Condition> preConditions = (i == 0 && j == -1) ? new HashSet<>() : symbol.getPreConditions();
            setTransition(new NonterminalTransition(nonterminalSlot, currentSlot, slot, arguments, getConditions(preConditions)));

            currentSlot = slot;

            return null;
        }

        @Override
        public Void visit(Conditional symbol) {

            Symbol sym = symbol.getSymbol();
            Expression expression = symbol.getExpression();

            visitSymbol(sym);

            BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), null, null, null);
            Transition transition = new ConditionalTransition(expression, currentSlot, thenSlot);
            setTransition(transition);
            currentSlot = thenSlot;

            return null;
        }

        @Override
        public Void visit(Code symbol) {

            Symbol sym = symbol.getSymbol();
            Statement[] statements = symbol.getStatements();

            visitSymbol(sym);

            BodyGrammarSlot done = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), null, null, null);
            Transition transition = new CodeTransition(statements, currentSlot, done);
            setTransition(transition);
            currentSlot = done;

            return null;
        }

        public Void visit(Return symbol) {
            BodyGrammarSlot done;
            if (i != rule.size() - 1)
                throw new RuntimeException("Return symbol can only be used at the end of a grammar rule!");
            else {
                if (rule.size() == 1)
                    done = new EpsilonGrammarSlot(rule.getPosition(i + 1), head, epsilonSlot, ConditionsFactory.DEFAULT);
                else
                    done = getEndSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
            }

            ReturnTransition transition = new ReturnTransition(symbol.getExpression(), currentSlot, done);
            setTransition(transition);
            currentSlot = done;

            return null;
        }

        @Override
        public Void visit(Terminal symbol) {
            TerminalGrammarSlot terminalSlot = getTerminalGrammarSlot(symbol);

            BodyGrammarSlot slot;

            if (i == rule.size() - 1 && j == -1)
                slot = getEndSlot(rule, i + 1, rule.getPosition(i + 1), head, symbol.getLabel(), null, null);
            else
                slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), symbol.getLabel(), null, null);

            Set<Condition> preConditions = (i == 0 && j == -1) ? Collections.emptySet() : symbol.getPreConditions();
            TerminalTransition transition = getTerminalTransition(terminalSlot, currentSlot, slot, preConditions, symbol.getPostConditions());
            setTransition(transition);
            currentSlot = slot;

            return null;
        }

        /**
         * Introduces epsilon transitions to handle labels and preconditions/postconditions
         */
        private void visitSymbol(Symbol symbol) {

            if (symbol instanceof Nonterminal || symbol instanceof Terminal || symbol instanceof Return) { // TODO: I think this can be unified
                symbol.accept(this);
                return;
            }

            Conditions preconditions = i == 0 ? ConditionsFactory.DEFAULT : getConditions(symbol.getPreConditions());

            if (symbol.getLabel() != null) {
                BodyGrammarSlot declared = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), null, null, null);
                EpsilonTransition transition = new EpsilonTransition(Type.DECLARE_LABEL, symbol.getLabel(), preconditions, currentSlot, declared);
                setTransition(transition);
                currentSlot = declared;
            } else {
                BodyGrammarSlot checked = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), null, null, null);
                EpsilonTransition transition = new EpsilonTransition(preconditions, currentSlot, checked);
                setTransition(transition);
                currentSlot = checked;
            }

            j += 1;

            symbol.accept(this);

            j -= 1;

            if (symbol.getLabel() != null) {

                BodyGrammarSlot stored;
                if (i == rule.size() - 1 && j == -1)
                    stored = getEndSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
                else
                    stored = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), null, null, null);

                EpsilonTransition transition = new EpsilonTransition(Type.STORE_LABEL, symbol.getLabel(), getConditions(symbol.getPostConditions()), currentSlot, stored);
                setTransition(transition);
                currentSlot = stored;
            } else {

                BodyGrammarSlot checked;
                if (i == rule.size() - 1 && j == -1)
                    checked = getEndSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
                else
                    checked = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), null, null, null);

                EpsilonTransition transition = new EpsilonTransition(getConditions(symbol.getPostConditions()), currentSlot, checked);
                setTransition(transition);
                currentSlot = checked;
            }
        }

        private void setTransition(Transition transition) {
            transition.origin().setOutTransition(transition);
            transition.destination().setInTransition(transition);
        }

    }

    private TerminalTransition getTerminalTransition(TerminalGrammarSlot slot,
                                                     BodyGrammarSlot origin, BodyGrammarSlot dest,
                                                     Set<Condition> preConditions, Set<Condition> postConditions) {

        return new TerminalTransition(slot, origin, dest, getConditions(preConditions), getConditions(postConditions));
    }

    private TerminalGrammarSlot getTerminalGrammarSlot(Terminal t) {
        Conditions preConditions = getConditions(t.getTerminalPreConditions());
        Conditions postConditions = getConditions(t.getTerminalPostConditions());
        TerminalGrammarSlot terminalSlot = terminalsMap.computeIfAbsent(t, k -> new TerminalGrammarSlot(t, matcherFactory, preConditions, postConditions));
        add(terminalSlot);
        return terminalSlot;
    }

    private NonterminalGrammarSlot getNonterminalSlot(Nonterminal nonterminal) {
        NonterminalGrammarSlot ntSlot = nonterminalsMap.computeIfAbsent(nonterminal,
                k -> new NonterminalGrammarSlot(nonterminal));
        add(ntSlot);
        return ntSlot;
    }

    private BodyGrammarSlot getFirstGrammarSlot(Rule rule, NonterminalGrammarSlot nonterminal) {
        BodyGrammarSlot slot;

        if (rule.size() == 0) {
            slot = new EpsilonGrammarSlot(rule.getPosition(0, 0), nonterminal, epsilonSlot, ConditionsFactory.DEFAULT);
        } else {
            // TODO: This is not a final solution; in particular,
            //       not any precondition of the first symbol (due to labels) can currently be moved to the first slot.
            Set<Condition> preConditions = new HashSet<>(rule.symbolAt(0).getPreConditions());

            slot = new BodyGrammarSlot(rule.getPosition(0, 0), rule.symbolAt(0).getLabel(), null, null, getConditions(preConditions));
        }
        add(slot);
        return slot;
    }

    private BodyGrammarSlot getBodyGrammarSlot(Rule rule, int i, Position position, String label, String variable, Set<String> state) {
        assert i < rule.size();

        BodyGrammarSlot slot;
        if (current != null)
            slot = new BodyGrammarSlot(position, label, (label != null && !label.isEmpty()) ? current.get(label) : -1,
                    variable, (variable != null && !variable.isEmpty()) ? current.get(variable) : -1, state, getConditions(rule.symbolAt(i - 1).getPostConditions()));
        else
            slot = new BodyGrammarSlot(position, label, variable, state, getConditions(rule.symbolAt(i - 1).getPostConditions()));

        add(slot);
        slot.setFollowTest(getFollowTest(rule, i));
        return slot;
    }

    private BodyGrammarSlot getEndSlot(Rule rule, int i, Position position, NonterminalGrammarSlot nonterminal, String label, String variable, Set<String> state) {
        assert i == rule.size();

        BodyGrammarSlot slot;
        if (current != null)
            slot = new EndGrammarSlot(position, nonterminal, label, (label != null && !label.isEmpty()) ? current.get(label) : -1,
                    variable, (variable != null && !variable.isEmpty()) ? current.get(variable) : -1, state, getConditions(rule.symbolAt(i - 1).getPostConditions()));
        else
            slot = new EndGrammarSlot(position, nonterminal, label, variable, state, getConditions(rule.symbolAt(i - 1).getPostConditions()));

        add(slot);
        slot.setFollowTest(getFollowTest(rule, i));
        return slot;
    }

    private void add(GrammarSlot slot) {
        slots.add(slot);
    }

    static private void validateNumberOfArguments(Nonterminal nonterminal, Expression[] arguments) {
        String[] parameters = nonterminal.getParameters();
        if ((parameters == null && arguments == null) || (Objects.requireNonNull(parameters).length == arguments.length)) return;

        throw new IncorrectNumberOfArgumentsException(nonterminal, arguments);
    }

    public void reset() {
        slots.forEach(GrammarSlot::reset);
    }

    private Conditions getConditions(Set<Condition> conditions) {
        if (conditions.isEmpty())
            return ConditionsFactory.DEFAULT;
        return ConditionsFactory.getConditions(conditions, matcherFactory);
    }
}
