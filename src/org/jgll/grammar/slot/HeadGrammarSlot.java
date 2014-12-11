package org.jgll.grammar.slot;

import java.io.PrintWriter;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.test.FollowTest;
import org.jgll.grammar.slot.test.PredictionTest;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.DummyNode;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

import static org.jgll.util.generator.GeneratorUtil.*;

/**
 * 
 * The grammar slot corresponding to the head of a rule.
 *
 * 
 * @author Ali Afroozeh
 * 
 */
public class HeadGrammarSlot implements GrammarSlot {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(HeadGrammarSlot.class);
	
	protected final Nonterminal nonterminal;
	
	private boolean nullable;
	
	protected BodyGrammarSlot firstSlots[];

	private final PredictionTest predictionTest;
	
	private final FollowTest followTest;
	
	private GSSNode[] gssNodes;

	private int altsCount;
	
	public HeadGrammarSlot(Nonterminal nonterminal, 
						   int altsCount, boolean nullable, 
						   PredictionTest predictionTest, FollowTest followTest) {
		this.nonterminal = nonterminal;
		this.altsCount = altsCount;
		this.firstSlots = new BodyGrammarSlot[altsCount];
		this.nullable = nullable;
		this.followTest = followTest;
		this.predictionTest = predictionTest;
	}
		
	public boolean isNullable() {
		return nullable;
	}
		
	public boolean test(int v) {
		return predictionTest.test(v);
	}
	
	public boolean testFollowSet(int v) {
		return followTest.test(v);
	}
	
	public Set<Integer> getPredictionSet(int v) {
		return predictionTest.get(v);
	}
	
	public void setFirstGrammarSlotForAlternate(BodyGrammarSlot slot, int index) {
		firstSlots[index] = slot;
	}
	
	public BodyGrammarSlot[] getFirstSlots() {
		return firstSlots;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		Set<Integer> set = predictionTest.get(lexer.charAt(ci));
		
		if (set == null) return null;
		
		// If there is only one alternative, skip descriptor creation and jump to the beginning
		// of the alternative
		if (set.size() == 1) {
			BodyGrammarSlot slot = firstSlots[set.iterator().next()];
			log.trace("Processing (%s, %d, %s, %s)", slot, parser.getCurrentInputIndex(), parser.getCurrentGSSNode(), parser.getCurrentSPPFNode());
			return slot;
		}
		
		for (int alternateIndex : set) {
			if (firstSlots[alternateIndex] == null) continue;  // TODO: remove these null alternatives altogether.
			parser.scheduleDescriptor(new Descriptor(firstSlots[alternateIndex], parser.getCurrentGSSNode(), ci, DummyNode.getInstance()));
		}
		
		return null;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
		
	@Override
	public String toString() {
		return nonterminal.toString();
	}
	
	@Override
	public GSSNode getGSSNode(int inputIndex) {
		GSSNode gssNode = new GSSNode(this, inputIndex);
		gssNodes[inputIndex] = gssNode;
		return gssNode;
	}

	@Override
	public GSSNode hasGSSNode(int inputIndex) {
		return gssNodes == null ? null : gssNodes[inputIndex];
	}

	@Override
	public void init(Input input) {
		gssNodes = new GSSNode[input.length()];
	}

	@Override
	public void reset() {
		gssNodes = null;
	}

	@Override
	public boolean isInitialized() {
		return gssNodes != null;
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new HeadGrammarSlot(")
		  .append("Nonterminal.withName(\"" + escape(nonterminal.getName()) + "\")" + ", ")
		  .append(altsCount + ", ")
		  .append(nullable + ", ")
		  .append(predictionTest.getConstructorCode(registry) + ", ")
		  .append(followTest.getConstructorCode(registry) + ")");
		return sb.toString();
	}
	
	@Override
	public void code(PrintWriter writer, GrammarSlotRegistry registry) {
		writer.println("// " + escape(nonterminal.getName()));
		writer.println("private final int slot" + registry.getId(this) + "() {");
		writer.println("  Set<Integer> set = slot" + registry.getId(this) + ".getPredictionSet(lexer.getInput().charAt(ci));");
		writer.println("  if (set == null) return L0;");
		writer.println("  if (set.size() == 1) {");
		writer.println("    log.trace(\"Processing (%s, %d, %s, %s)\", slot" + registry.getId(this) + ".getFirstSlots()[set.iterator().next()]" + ", ci, cu, cn);");
		writer.println("    return slot" + registry.getId(this) + ".getFirstSlots()[set.iterator().next()].getId();");
		writer.println("  }");
		writer.println("  for (int alternateIndex : set) {");
		writer.println("    if (slot" + registry.getId(this) + ".getFirstSlots()[alternateIndex] == null) continue;");
		writer.println("    scheduleDescriptor(new Descriptor(slot" + registry.getId(this) + ".getFirstSlots()[alternateIndex], cu, ci, DummyNode.getInstance()));");
		writer.println("  }");
		writer.println("  return L0;");
		writer.println("}");
	}

}
