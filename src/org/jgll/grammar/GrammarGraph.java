package org.jgll.grammar;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
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
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.logging.LoggerWrapper;

import com.google.common.collect.BiMap;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GrammarGraph implements Serializable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(GrammarGraph.class);
	
	private static final long serialVersionUID = 1L;
	
	private List<HeadGrammarSlot> headGrammarSlots;
	
	private BiMap<BodyGrammarSlot, Integer> slots;
	
	/**
	 * A map from nonterminal names to their corresponding head ‚slots.
	 * This map is used to locate head grammar slots by name for parsing
	 * from any arbitrary nonterminal.
	 */
	Map<Nonterminal, HeadGrammarSlot> nameToNonterminals;
	
	private  Map<String, BodyGrammarSlot> nameToSlots;
	
	private String name;
	
	private int longestTerminalChain;

	private BiMap<RegularExpression, Integer> regularExpressions;
	
	private Map<String, RegularExpression> regularExpressionNames;
	
	private Map<Nonterminal, Set<RegularExpression>> followSets;
	
	private Set<Nonterminal> ll1SubGrammarNonterminals;
	
	private BiMap<Nonterminal, Integer> nonterminals;
	
	private Map<Integer, RunnableAutomaton> runnableAutomatons;
	
	private Object[][] objects;
	
	private Grammar grammar;
	
	public GrammarGraph(GrammarGraphBuilder builder) {
		this.name = builder.name;
		this.headGrammarSlots = builder.headGrammarSlots;
		this.slots = builder.slots;
		this.nameToNonterminals = new HashMap<>();
		this.nameToNonterminals = builder.nonterminalsMap;
		
		this.regularExpressions = builder.regularExpressions;
		this.ll1SubGrammarNonterminals = builder.ll1SubGrammarNonterminals;
		
		this.objects = builder.objects;
		
		this.nonterminals = builder.nonterminals;

		this.regularExpressions = builder.regularExpressions;
		
		this.nameToSlots = new HashMap<>();
		for(BodyGrammarSlot slot : slots.keySet()) {
			nameToSlots.put(slot.getLabel(), slot);
		}
		
		regularExpressionNames = new HashMap<>();
		for(Entry<RegularExpression, Integer> entry : regularExpressions.entrySet()) {
			regularExpressionNames.put(entry.getKey().getName(), entry.getKey());
		}
		
		runnableAutomatons = new HashMap<>();
		for (RegularExpression regex : regularExpressions.keySet()) {
			runnableAutomatons.put(regularExpressions.get(regex), regex.getAutomaton().getRunnableAutomaton());
		}
		
		grammar = builder.grammar;
		
		printGrammarStatistics();
	}
	
	public void printGrammarStatistics() {
		log.info("Grammar information:");
		log.info("Nonterminals: %d", headGrammarSlots.size());
		log.info("Production rules: %d", grammar.sizeRules());
		log.info("Grammar slots: %d", slots.size());
		log.debug("Longest terminal Chain: %d", longestTerminalChain);
	}
	
	public String getCode() {
		StringBuilder sb = new StringBuilder();

		sb.append("private static final LoggerWrapper log = LoggerWrapper.getLogger(Test.class);").append(NL);
		sb.append("private static final int L0 = -1;").append(NL);
		sb.append(NL);
		
		// GLL fields
		sb.append("private int cs;").append(" // Current grammar slot").append(NL);
		sb.append(NL);
		
		// Generate field declarations
		for (HeadGrammarSlot head : headGrammarSlots) {
			for (BodyGrammarSlot slot : head.getFirstSlots()) {
				sb.append("@SuppressWarnings(\"unchecked\")").append(NL)
				  .append("HeadGrammarSlot slot" + head.getId() + " = ").append(head.getConstructorCode() + ";").append(NL);
				BodyGrammarSlot current = slot;
				while (current != null) {
					sb.append("BodyGrammarSlot slot" + current.getId() + " = ").append(current.getConstructorCode() + ";").append(NL);
					current = current.next();
				}
			}
		}
		
		sb.append(NL);
		
		sb.append("@Override").append(NL)
		  .append("protected void parse(HeadGrammarSlot startSymbolName) {").append(NL)
		  .append(TAB).append("while (true) {").append(NL)
		  .append(TAB).append(TAB).append("switch (cs) {").append(NL);

		sb.append("case L0:").append(NL)
		  .append(TAB).append("if (hasNextDescriptor()) {").append(NL)
		  .append(TAB).append(TAB).append("Descriptor descriptor = nextDescriptor();").append(NL)
		  .append(TAB).append(TAB).append("log.trace(\"Processing %s\", descriptor);").append(NL)
		  .append(TAB).append(TAB).append("cu = descriptor.getGSSNode();").append(NL)
		  .append(TAB).append(TAB).append("ci = descriptor.getInputIndex();").append(NL)
		  .append(TAB).append(TAB).append("cn = descriptor.getSPPFNode();").append(NL)
		  .append(TAB).append(TAB).append("cs = descriptor.getGrammarSlot().getId();").append(NL)
		  .append(TAB).append(TAB).append("break;").append(NL)
		  .append(TAB).append("} else {").append(NL)
		  .append(TAB).append(TAB).append("return;").append(NL)
		  .append(TAB).append("}").append(NL)
		  .append(NL);
		
		// Generate the body of switch case
		for (HeadGrammarSlot head : headGrammarSlots) {
			head.code(sb);
			for (BodyGrammarSlot slot : head.getFirstSlots()) {
				BodyGrammarSlot current = slot;
				while (current != null) {
					current.code(sb);
					current = current.next();
				}
			}
		}
		
		sb.append(TAB).append(TAB).append("}").append(NL);
		sb.append(TAB).append("}").append(NL);
		sb.append("}").append(NL);
		
		return sb.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public HeadGrammarSlot getHeadGrammarSlot(int id) {
		return headGrammarSlots.get(id);
	}
	
	public BodyGrammarSlot getGrammarSlot(int id) {
		return slots.inverse().get(id);
	}
		
	public List<HeadGrammarSlot> getNonterminals() {
		return headGrammarSlots;
	}
	
	public HeadGrammarSlot getHeadGrammarSlot(String name) {
		return nameToNonterminals.get(Nonterminal.withName(name));
	}
	
	private String getSlotName(BodyGrammarSlot slot) {
		if(slot instanceof TokenGrammarSlot) {
			return ((TokenGrammarSlot) slot).getSymbol().getName();
		}
		else if (slot instanceof NonterminalGrammarSlot) {
			return ((NonterminalGrammarSlot) slot).getNonterminal().getNonterminal().toString();
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
				sb.append(head.getNonterminal()).append(" ::= ");
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
	
	public RunnableAutomaton getAutomaton(int index) {
		return runnableAutomatons.get(index);
	}
	
	public int getCountTokens() {
		return regularExpressions.size();
	}
	
	public Iterable<RegularExpression> getTokens() {
		return regularExpressions.keySet();
	}
	
	public Nonterminal getNonterminalById(int index) {
		return nonterminals.inverse().get(index);
	}
	
	public int getNonterminalId(Nonterminal nonterminal) {
		return nonterminals.get(nonterminal);
	}
	
	public RegularExpression getRegularExpressionById(int index) {
		return regularExpressions.inverse().get(index);
	}
	
	public int getRegularExpressionId(RegularExpression regex) {
		return regularExpressions.get(regex);
	}
	
	public Set<BodyGrammarSlot> getGrammarSlots() {
		return slots.keySet();
	}
	
	public RegularExpression getRegularExpressionByName(String name) {
		return regularExpressionNames.get(name);
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
	
	public Set<RegularExpression> getFollowSet(Nonterminal nonterminal) {
		return followSets.get(nonterminal);
	}
	
	public Object getObject(int nonterminalId, int alternateId) {
		return objects[nonterminalId][alternateId];
	}
	
	public int getCountAlternates(Nonterminal nonterminal) {
		return grammar.getAlternatives(nonterminal).size();
	}
	
	public List<Symbol> getDefinition(Nonterminal nonterminal, int alternateIndex) {
		List<Symbol> list = null;
		Iterator<List<Symbol>> it = grammar.getAlternatives(nonterminal).iterator();
		for(int i = 0; i <= alternateIndex; i++) {
			list = it.next();
		}
		return list;
	}
	
	public void reset() {
		for (HeadGrammarSlot head : headGrammarSlots) {
			head.reset();
			for (BodyGrammarSlot slot : head.getFirstSlots()) {
				BodyGrammarSlot current = slot;
				while (current != null) {
					current.reset();
					current = current.next();
				}
			}
		}
	}
	
}
