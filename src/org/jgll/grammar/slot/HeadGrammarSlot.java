package org.jgll.grammar.slot;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.io.PrintWriter;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.test.FollowTest;
import org.jgll.grammar.slot.test.PredictionTest;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

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

	protected final PredictionTest predictionTest;
	
	protected final FollowTest followTest;
	
	private GSSNode[] gssNodes;

	protected final int altsCount;
	
	private int id;
	
	public HeadGrammarSlot(int id, HeadGrammarSlot slot) {
		this(slot.nonterminal, slot.altsCount, slot.nullable, slot.predictionTest, slot.followTest);
		this.id = id;
	}
	
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
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		int ci = parser.getCurrentInputIndex();
		
		Set<Integer> set = predictionTest.get(input.charAt(ci));
		
		if (set == null) return;
		
		// If there is only one alternative, skip descriptor creation and jump to the beginning
		// of the alternative
		if (set.size() == 1) {
			BodyGrammarSlot slot = firstSlots[set.iterator().next()];
			log.trace("Processing (%s, %d, %s, %s)", slot, parser.getCurrentInputIndex(), parser.getCurrentGSSNode(), parser.getCurrentSPPFNode());
			return;
		}
		
		for (int alternateIndex : set) {
			if (firstSlots[alternateIndex] == null) continue;  // TODO: remove these null alternatives altogether.
			parser.scheduleDescriptor(new Descriptor(firstSlots[alternateIndex], parser.getCurrentGSSNode(), ci, DummyNode.getInstance()));
		}
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
		  .append(followTest.getConstructorCode(registry) + ")")
		  .append(".withId(").append(registry.getId(this)).append(")");
		return sb.toString();
	}
	
	@Override
	public void code(PrintWriter writer, GrammarSlotRegistry registry) {
		writer.println("// " + escape(nonterminal.getName()));
		writer.println("private final int slot" + registry.getId(this) + "() {");
		writer.println("  Set<Integer> set = slot" + registry.getId(this) + ".getPredictionSet(lexer.charAt(ci));");
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
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public HeadGrammarSlot withId(int id) {
		return new HeadGrammarSlot(id, this);
	}

}
