package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements Serializable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(Grammar.class);
	
	private static final long serialVersionUID = 1L;
	
	private List<HeadGrammarSlot> nonterminals;
	
	private List<BodyGrammarSlot> slots;
	
	/**
	 * A map from nonterminal names to their corresponding head slots.
	 * This map is used to locate head grammar slots by name for parsing
	 * from any arbitrary nonterminal.
	 */
	Map<String, HeadGrammarSlot> nameToNonterminals;
	
	private Map<String, BodyGrammarSlot> nameToSlots;
	
	private List<HeadGrammarSlot> newNonterminals;
	
	private String name;

	private int longestTerminalChain;
	
	private int maximumNumAlternates;
	
	private int maxDescriptorsAtInput;
	
	private int averageDescriptorsAtInput;
	
	private int stDevDescriptors;
	
	public Grammar(GrammarBuilder builder) {
		this.name = builder.name;
		this.nonterminals = builder.nonterminals;
		this.slots = builder.slots;
		this.nameToNonterminals = new HashMap<>();
		this.nameToSlots = new HashMap<>();
		this.nameToNonterminals = builder.nonterminalsMap;
		
		for(BodyGrammarSlot slot : slots) {
			nameToSlots.put(slot.toString(), slot);
		}
		
		this.newNonterminals = builder.newNonterminals;
		this.longestTerminalChain = builder.longestTerminalChain;
		this.maximumNumAlternates = builder.maximumNumAlternates;
		this.maxDescriptorsAtInput = builder.maxDescriptors;
		this.averageDescriptorsAtInput = builder.averageDescriptors;
		this.stDevDescriptors = (int) builder.stDevDescriptors;
		
		printGrammarStatistics();
	}

	public void printGrammarStatistics() {
		log.info("Grammar information:");
		log.info("Nonterminals: %d", nonterminals.size());
		log.info("Production rules: %d", numProductions());
		log.info("Grammar slots: %d", slots.size());
		log.debug("Longest terminal Chain: %d", longestTerminalChain);
		log.debug("Maximum number: %d", maximumNumAlternates);
		log.debug("Maximum descriptors: %d", maxDescriptorsAtInput);
		log.debug("Average descriptors: %d", averageDescriptorsAtInput);
		log.debug("Standard Deviation descriptors: %d", stDevDescriptors);
	}
	
	private int numProductions() {
		int num = 0;
		for(HeadGrammarSlot head : nonterminals) {
			num += head.getCountAlternates();
		}
		return num;
	}
	
	public void code(Writer writer, String packageName) throws IOException {
	
		String header = Input.read(this.getClass().getResourceAsStream("ParserTemplate"));
		header = header.replace("${className}", name).replace("${packageName}", packageName);
		writer.append(header);
		
		// case L0:
		writer.append("case " + L0.getInstance().getId() + ":\n");
		L0.getInstance().codeParser(writer);
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			writer.append("// " + nonterminal + "\n");
			writer.append("case " + nonterminal.getId() + ":\n");
			writer.append("parse_" + nonterminal.getId() + "();\n");
			writer.append("break;\n");
		}
				
		for(BodyGrammarSlot slot : slots) {
			if(!(slot.previous instanceof TerminalGrammarSlot)) {
				writer.append("// " + slot + "\n");
				writer.append("case " + slot.getId() + ":\n");
				writer.append("parse_" + slot.getId() + "();\n");
				writer.append("break;\n");
			}
		}
		
		writer.append("} } }\n");
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			nonterminal.codeParser(writer);
		}
		
		writer.append("}");
	}
	
	public String getName() {
		return name;
	}
	
	public HeadGrammarSlot getNonterminal(int id) {
		return nonterminals.get(id);
	}
		
	public BodyGrammarSlot getGrammarSlot(int id) {
		return slots.get(id);
	}
		
	public List<HeadGrammarSlot> getNonterminals() {
		return nonterminals;
	}
	
	public List<BodyGrammarSlot> getGrammarSlots() {
		return slots;
	}
	
	public HeadGrammarSlot getNonterminalByName(String name) {
		return nameToNonterminals.get(name);
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
		
		GrammarVisitor visitor = new GrammarVisitor( new GrammarVisitAction() {
			
			@Override
			public void visit(LastGrammarSlot slot) {
				sb.append("\n");
			}
			
			@Override
			public void visit(TerminalGrammarSlot slot) {
				sb.append(" ").append(getName(slot));
			}
			
			@Override
			public void visit(NonterminalGrammarSlot slot) {
				sb.append(" ").append(getName(slot));
			}
			
			@Override
			public void visit(HeadGrammarSlot head) {
				sb.append(getName(head)).append(" ::= ");
			}
		});
		
		// The first nonterminal is the starting point
		// TODO: allow the user to specify the root of a grammar
		for(HeadGrammarSlot head : nonterminals) {
			visitor.visit(head);
		}

		return sb.toString();
	}
	
	private String getName(BodyGrammarSlot slot) {
		if(slot instanceof NonterminalGrammarSlot) {
			return getName(((NonterminalGrammarSlot) slot).getNonterminal());
		}
		
		return slot.getSymbol().toString();
	}
	
	/**
	 * If the nonterminal is introduced while rewriting, adds its index to it. 
	 */
	private String getName(HeadGrammarSlot head) {
		return newNonterminals.contains(head) ? head.getNonterminal().getName() + (newNonterminals.indexOf(head) + 1) : head.getNonterminal().getName();			
	}
	
}
