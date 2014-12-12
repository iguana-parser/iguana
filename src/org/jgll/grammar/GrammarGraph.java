package org.jgll.grammar;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GrammarGraph implements Serializable {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(GrammarGraph.class);
	
	private static final long serialVersionUID = 1L;
	
	private GrammarSlotRegistry registry;

	private List<HeadGrammarSlot> headGrammarSlots;
	
	private Set<BodyGrammarSlot> slots;
	
	private List<TerminalGrammarSlot> terminals;
	
	private String name;
	
	private Map<Nonterminal, Set<RegularExpression>> followSets;
	
	private Grammar grammar;
	
	public GrammarGraph(GrammarGraphBuilder builder) {
		this.name = builder.name;
		this.registry = new GrammarSlotRegistry(builder.nonterminalsMap, builder.terminalsMap, builder.slots);
		this.headGrammarSlots = new ArrayList<>(builder.nonterminalsMap.values());
		this.terminals = new ArrayList<>(builder.terminalsMap.values());
		this.slots = new HashSet<>(builder.slots.values());
		grammar = builder.grammar;
		printGrammarStatistics();
	}
	
	public void printGrammarStatistics() {
		log.info("Grammar information:");
		log.info("Nonterminals: %d", headGrammarSlots.size());
		log.info("Production rules: %d", grammar.sizeRules());
		log.info("Grammar slots: %d", slots.size());
	}
	
	public void generate(PrintWriter writer) {
		generate("test", "Test", writer);
	}
	
	public void generate(String packageName, String className, PrintWriter writer) {
		
		writer.println("package " + packageName + ";");
		writer.println();
		
		// Imports
		writer.println("import java.util.*;");
		writer.println("import org.jgll.grammar.slot.*;");
		writer.println("import org.jgll.grammar.slot.specialized.*;");
		writer.println("import org.jgll.grammar.slot.nodecreator.*;");
		writer.println("import org.jgll.grammar.slot.test.*;");
		writer.println("import org.jgll.grammar.symbol.Character;");
		writer.println("import org.jgll.grammar.symbol.*;");
		writer.println("import org.jgll.regex.*;");
		writer.println("import org.jgll.parser.NewGLLParserImpl;");
		writer.println("import org.jgll.parser.descriptor.Descriptor;");
		writer.println("import org.jgll.parser.lookup.factory.*;");
		writer.println("import org.jgll.sppf.*;");
		writer.println("import org.jgll.grammar.condition.*;");
		writer.println("import org.jgll.util.logging.LoggerWrapper;");
		writer.println("import com.google.common.collect.Sets;");
		writer.println("import static org.jgll.grammar.condition.ConditionType.*;");
		writer.println("import static org.jgll.util.CollectionsUtil.*;");
		writer.println();

		// Class definition
		writer.println("@SuppressWarnings(\"unchecked\")");
		writer.println("public class " + className + " extends NewGLLParserImpl {");
		writer.println();
		
		writer.println("private static final LoggerWrapper log = LoggerWrapper.getLogger(" + className + ".class);");
		writer.println("private static final int L0 = -1;");
		writer.println();
		
		// GLL fields
		writer.println("private int cs; // Current grammar slot");
		writer.println();
		
		writer.println("private Map<String, Integer> startSymbols = new HashMap<>();");
		
		// Generate Head grammar slots. 
		for (HeadGrammarSlot head : headGrammarSlots) {
			writer.println("private HeadGrammarSlot slot" + registry.getId(head) + ";");
		}
		
		// Generate body grammar slots
		for (HeadGrammarSlot head : headGrammarSlots) {
			for (BodyGrammarSlot slot : head.getFirstSlots()) {
				BodyGrammarSlot current = slot;
				while (current != null) {
					writer.println("private BodyGrammarSlot slot" + registry.getId(current) + ";");
					current = current.next();
				}
			}
		}
		
		writer.println();
		
		// Init body grammar slot method
		ArrayList<BodyGrammarSlot> bodyGrammarSlots = new ArrayList<>(slots);
		int n = slots.size() / 200;
		int r = slots.size() % 200;
		
		// Constructor
		writer.println("public " + className + "() {");
		writer.println("  super(new DistributedGSSLookupFactory(), new NewSPPFLookupFactory(), new DefaultDescriptorLookupFactory());");
		
        for (int i = 0; i < n; i++) {
            writer.println("  initBodyGrammarSlots" + i + "();");
        }
        if (r > 0) {
            writer.println("  initBodyGrammarSlots" + n + "();");
        }
		
		// Create alternative links
		for (HeadGrammarSlot head : headGrammarSlots) {
			int i = 0;
			for (BodyGrammarSlot slot : head.getFirstSlots()) {
				writer.println("  slot" + registry.getId(head) + ".setFirstGrammarSlotForAlternate(slot" + registry.getId(slot) + ", " + i++ + ");");
			}
		}
		
		// Add start symbols
		for (HeadGrammarSlot head : headGrammarSlots) {
			writer.println("  startSymbols.put(\"" + escape(head.getNonterminal().getName()) + "\", " + registry.getId(head) + ");");
		}

		writer.println("}");
		writer.println();
		// End constructor
		
		// Init heads method
		writer.println("private void initHeadGrammarSlots() {");
		for (HeadGrammarSlot head : headGrammarSlots) {
			writer.println("  slot" + registry.getId(head) + " = " + head.getConstructorCode(registry) + ";");
		}
		writer.println("}");
		writer.println();
		// end init heads
		
		// Init terminals
		writer.println("private void initTerminalGrammarSlots() {");
		for (TerminalGrammarSlot terminal : terminals) {
			writer.println("  slot" + registry.getId(terminal) + " = " + terminal.getConstructorCode(registry) + ";");
		}
		writer.println("}");
		writer.println();
		
		
		for (int i = 0; i < n; i++) {
			writer.println("private void initBodyGrammarSlots" + i + "() {");
			// Generate body grammar slots
			
			for (int j = i * 200; j < (i + 1) * 200; j++ ) {
				BodyGrammarSlot current = bodyGrammarSlots.get(j);
				writer.println("  slot" + registry.getId(current) + " = " + current.getConstructorCode(registry) + ";");
			}
			
			writer.println("}");
			writer.println();
		}
		
		// The remaining
		writer.println("private void initBodyGrammarSlots" + n + "() {");
		for (int j = n * 200; j < bodyGrammarSlots.size(); j++ ) {
			BodyGrammarSlot current = bodyGrammarSlots.get(j);
			writer.println("  slot" + registry.getId(current) + " = " + current.getConstructorCode(registry) + ";");
		}
		writer.println("}");
		writer.println();
		
		// end init


		// Overriding the getStartSymbol method
		writer.println("@Override");
		writer.println("protected HeadGrammarSlot getStartSymbol(String name) {");
		writer.println("    cs = startSymbols.get(name);");
		writer.println("    return slot" + registry.getId(headGrammarSlots.get(0)) + ";");
		writer.println("}");

		writer.println();
		
		writer.println("@Override");
		writer.println("protected void parse(HeadGrammarSlot startSymbolName) {");
		writer.println("  while (true) {");
		writer.println("    switch (cs) {");

		writer.println("case L0:");
		writer.println("  if (hasNextDescriptor()) {");
		writer.println("    Descriptor descriptor = nextDescriptor();");
		writer.println("    log.trace(\"Processing %s\", descriptor);");
		writer.println("    cu = descriptor.getGSSNode();");
		writer.println("    ci = descriptor.getInputIndex();");
		writer.println("    cn = descriptor.getSPPFNode();");
		writer.println("    cs = descriptor.getGrammarSlot().getId();");
		writer.println("    break;");
		writer.println("  } else {");
		writer.println("    return;");
		writer.println("  }");
		writer.println();
		
		// Generate the body of switch case
		for (HeadGrammarSlot head : headGrammarSlots) {
			writer.println("// " + escape(head.toString()));
			writer.println("case " + registry.getId(head) + ":");
			writer.println("  cs = slot" + registry.getId(head) + "();");
			writer.println("  break;");
			writer.println();
		}
		
		if (bodyGrammarSlots.size() > 2500) {
			int mainLoops = bodyGrammarSlots.size() / 2500;
		} else {
			generateCases(writer, bodyGrammarSlots, 0, bodyGrammarSlots.size());
		}
				
		writer.println("    }");
		writer.println("  }");
		writer.println("}");
		writer.println();
		// End parse method
		
		// Grammar slot methods

		for (HeadGrammarSlot head : headGrammarSlots) {
				
			head.code(writer, registry);
			writer.println();
						
			for (BodyGrammarSlot slot : head.getFirstSlots()) {
				BodyGrammarSlot current = slot;
				while (current != null) {
					current.code(writer, registry);
					writer.println();
					current = current.next();
				}
			}
		}
		writer.println("}");
	}
	
	
	private void generateCases(PrintWriter writer, List<BodyGrammarSlot> slots, int start, int end) {
		
		for (int i = start; i < end; i++) {
			BodyGrammarSlot current = slots.get(i);
			
			writer.println("// " + escape(current.toString()));
			writer.println("case " + registry.getId(current) + ":");
			if (current.getClass() == TokenGrammarSlot.class) {
				writer.println("  slot" + registry.getId(current) + "();");
			} else {
				writer.println("  cs = slot" + registry.getId(current) + "();");
				writer.println("  break;");						
			}
			writer.println();
		}
		
	}
	
	public String getName() {
		return name;
	}
	
	public HeadGrammarSlot getHeadGrammarSlot(int id) {
		return headGrammarSlots.get(id);
	}
	
	public List<HeadGrammarSlot> getNonterminals() {
		return headGrammarSlots;
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
	
	public Set<BodyGrammarSlot> getGrammarSlots() {
		return slots;
	}
	
	public Set<RegularExpression> getFollowSet(Nonterminal nonterminal) {
		return followSets.get(nonterminal);
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
	
	public GrammarSlotRegistry getRegistry() {
		return registry;
	}
	
}
