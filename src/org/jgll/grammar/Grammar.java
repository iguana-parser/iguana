package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements Serializable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(Grammar.class);
	
	private static final long serialVersionUID = 1L;
	
	private List<HeadGrammarSlot> headGrammarSlots;
	
	private List<BodyGrammarSlot> slots;
	
	/**
	 * A map from nonterminal names to their corresponding head ‚slots.
	 * This map is used to locate head grammar slots by name for parsing
	 * from any arbitrary nonterminal.
	 */
	Map<Nonterminal, HeadGrammarSlot> nameToNonterminals;
	
	private transient Map<String, BodyGrammarSlot> nameToSlots;
	
	private String name;
	
	private int longestTerminalChain;
	
	private int maximumNumAlternates;
	
	private int maxDescriptorsAtInput;
	
	private int averageDescriptorsAtInput;
	
	private int stDevDescriptors;
	
	private Map<HeadGrammarSlot, Set<HeadGrammarSlot>> reachabilityGraph;
	
	private Map<RegularExpression, Integer> tokenIDMap;
	
	private List<RegularExpression> tokens;
	
	private List<Matcher> matchers;
	
	private Matcher[] dfas;
	
	private Map<Nonterminal, Set<RegularExpression>> firstSets;
	
	private Map<Nonterminal, Set<RegularExpression>> followSets;
	
	private Set<Nonterminal> ll1SubGrammarNonterminals;
	
	private Map<Nonterminal, List<Set<RegularExpression>>> predictionSets;
	
	private Map<Nonterminal, Integer> nonterminalIds;
	
	private List<Nonterminal> nonterminals;
	
	private Object[][] objects;
	
	private Map<Nonterminal, Set<List<Symbol>>> definitions;
	
	private Map<List<Symbol>, Integer> intermediateNodeIds;
	
	private Map<Integer, List<Symbol>> reverseIntermediateNodeIds;
	
	public Grammar(GrammarBuilder builder) {
		this.name = builder.name;
		this.headGrammarSlots = builder.headGrammarSlots;
		this.slots = builder.slots;
		this.nameToNonterminals = new HashMap<>();
		this.nameToNonterminals = builder.nonterminalsMap;
		
		this.maximumNumAlternates = builder.maximumNumAlternates;
		this.maxDescriptorsAtInput = builder.maxDescriptors;
		this.averageDescriptorsAtInput = builder.averageDescriptors;
		this.stDevDescriptors = (int) builder.stDevDescriptors;
		this.reachabilityGraph = builder.directReachabilityGraph;
		this.tokens = builder.tokens;
		this.dfas = builder.dfas;
		this.firstSets = builder.firstSets;
		this.followSets = builder.followSets;
		this.ll1SubGrammarNonterminals = builder.ll1SubGrammarNonterminals;
		
		this.objects = builder.objects;
		
		this.nonterminalIds = builder.nonterminalIds;
		this.nonterminals = builder.nonterminals;

		this.tokenIDMap = builder.tokenIDMap;
		
		this.matchers = new ArrayList<>();
		for(RegularExpression regex : tokens) {
			matchers.add(regex.toAutomaton().getMatcher());
		}
		
		this.nameToSlots = new HashMap<>();
		for(BodyGrammarSlot slot : slots) {
			nameToSlots.put(slot.getLabel(), slot);
		}
		
		definitions = builder.definitions;
		intermediateNodeIds = builder.intermediateNodeIds;
		
		reverseIntermediateNodeIds = new HashMap<>();
		for(Entry<List<Symbol>, Integer> e : intermediateNodeIds.entrySet()) {
			reverseIntermediateNodeIds.put(e.getValue(), e.getKey());
		}
		
		printGrammarStatistics();
	}
	
	public void printGrammarStatistics() {
		log.info("Grammar information:");
		log.info("Nonterminals: %d", headGrammarSlots.size());
		log.info("Production rules: %d", numProductions());
		log.info("Grammar slots: %d", slots.size());
		log.debug("Longest terminal Chain: %d", longestTerminalChain);
		log.debug("Maximum number alternates: %d", maximumNumAlternates);
		log.debug("Maximum descriptors: %d", maxDescriptorsAtInput);
		log.trace("Average descriptors: %d", averageDescriptorsAtInput);
		log.trace("Standard Deviation descriptors: %d", stDevDescriptors);
	}
	
	public Set<HeadGrammarSlot> getReachableNonterminals(HeadGrammarSlot nonterminal) {
		return reachabilityGraph.get(nonterminal);
	}
	
	private int numProductions() {
		int num = 0;
		for(HeadGrammarSlot head : headGrammarSlots) {
			num += head.getCountAlternates();
		}
		return num;
	}
	
	public void code(Writer writer, String packageName) throws IOException {
	}
	
	public String getName() {
		return name;
	}
	
	public HeadGrammarSlot getHeadGrammarSlot(int id) {
		return headGrammarSlots.get(id);
	}
	
	public BodyGrammarSlot getGrammarSlot(int id) {
		return slots.get(id);
	}
		
	public List<HeadGrammarSlot> getNonterminals() {
		return headGrammarSlots;
	}
	
	public List<BodyGrammarSlot> getGrammarSlots() {
		return slots;
	}
	
	public HeadGrammarSlot getHeadGrammarSlot(String name) {
		return nameToNonterminals.get(new Nonterminal(name));
	}
	
	private String getSlotName(BodyGrammarSlot slot) {
		if(slot instanceof TokenGrammarSlot) {
			return ((TokenGrammarSlot) slot).getSymbol().getName();
		}
		else if (slot instanceof NonterminalGrammarSlot) {
			return ((NonterminalGrammarSlot) slot).getNonterminal().getNonterminal().getName();
		} 
		else {
			return "";
		}
	}
	
	public BodyGrammarSlot getGrammarSlotByName(String name) {
		return nameToSlots.get(name);
	}
	
	public int getLongestTerminalChain() {
		return longestTerminalChain;
	}
	
	public int getMaximumNumAlternates() {
		return maximumNumAlternates;
	}
	
	public int getMaxDescriptorsAtInput() {
		return maxDescriptorsAtInput;
	}
	
	public int getAverageDescriptorsAtInput() {
		return averageDescriptorsAtInput;
	}
	
	public int getStDevDescriptors() {
		return stDevDescriptors;
	}
	
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		
		GrammarVisitAction action = new GrammarVisitAction() {
			
			@Override
			public void visit(LastGrammarSlot slot) {
				if(slot instanceof EpsilonGrammarSlot) {
					sb.append(" epsilon\n");
				} else {
					sb.append("\n");
				}
			}
			
			@Override
			public void visit(NonterminalGrammarSlot slot) {
				sb.append(" ").append(getSlotName(slot));
			}
			
			@Override
			public void visit(HeadGrammarSlot head) {
				sb.append(head.getNonterminal().getName()).append(" ::= ");
			}

			@Override
			public void visit(TokenGrammarSlot slot) {
				sb.append(" ").append(getSlotName(slot));
			}

		};
		
		// The first nonterminal is the starting point
		// TODO: allow the user to specify the root of a grammar
		GrammarVisitor.visit(this, action);
		return sb.toString();
	}
	
	public Matcher getMatcher(int index) {
		return matchers.get(index);
	}
	
	public int getCountTokens() {
		return tokenIDMap.size() + 2;
	}
	
	public Iterable<RegularExpression> getTokens() {
		return tokenIDMap.keySet();
	}
	
	public Nonterminal getNonterminalById(int index) {
		return nonterminals.get(index);
	}
	
	public int getNonterminalId(Nonterminal nonterminal) {
		return nonterminalIds.get(nonterminal);
	}
	
	public RegularExpression getRegularExpressionById(int index) {
		return tokens.get(index);
	}
	
	public int getRegularExpressionId(RegularExpression regex) {
		return tokenIDMap.get(regex);
	}
	
	public int getIntermediateNodeId(Symbol...symbols) {
		return getIntermediateNodeId(Arrays.asList(symbols));
	}
	
	public int getIntermediateNodeId(List<Symbol> symbols) {
		return intermediateNodeIds.get(symbols);
	}
	
	public List<Symbol> getIntermediateNodeSequence(int id) {
		return reverseIntermediateNodeIds.get(id);
	}
	
	public int getCountLL1Nonterminals() {
		int count = 0;
		for(HeadGrammarSlot head : headGrammarSlots) {
			if(isLL1SubGrammar(head.getNonterminal())) {
				count++;
			}
		}
		return count;
	}
	
	public boolean isLL1SubGrammar(Nonterminal nonterminal) {
		return ll1SubGrammarNonterminals.contains(nonterminal);
	}
	
	public Matcher getDFA(int id) {
		return dfas[id];
	}
	
	public Set<RegularExpression> getFirstSet(Nonterminal nonterminal) {
		return firstSets.get(nonterminal);
	}
	
	public Set<RegularExpression> getPredictionSetForAlternate(Nonterminal nonterminal, int index) {
		return predictionSets.get(nonterminal).get(index);
	}
	 
	public Set<RegularExpression> getFollowSet(Nonterminal nonterminal) {
		return followSets.get(nonterminal);
	}
	
	public Object getObject(int nonterminalId, int alternateId) {
		return objects[nonterminalId][alternateId];
	}
	
	public int getCountAlternates(Nonterminal nonterminal) {
		return definitions.get(nonterminal).size();
	}
}
