package org.jgll.grammar.slot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.lookup.NodeLookup;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.lookup.NodeAddedAction;
import org.jgll.util.Input;


/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends AbstractGrammarSlot {
	
	private final Nonterminal nonterminal;
	
	private final List<BodyGrammarSlot> firstSlots;
	
	private final NodeLookup nodeLookup;

	private Map<NonterminalNode, NonterminalNode> nonterminalNodes;

	public NonterminalGrammarSlot(Nonterminal nonterminal, NodeLookup nodeLookup) {
		this.nonterminal = nonterminal;
		this.nodeLookup = nodeLookup;
		this.firstSlots = new ArrayList<>();
		this.nonterminalNodes = new HashMap<>(1000);
	}
	
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		firstSlots.forEach(s -> parser.scheduleDescriptor(new Descriptor(s, u, i, DummyNode.getInstance())));
	}
	
	public void addFirstSlot(BodyGrammarSlot slot) {
		firstSlots.add(slot);
	}
	
	public List<BodyGrammarSlot> getFirstSlots() {
		return firstSlots;
	}
	
	public boolean test(int v)  {
		return true;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
	
	@Override
	public String toString() {
		return nonterminal.getName();
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return new StringBuilder()
		           .append("new NonterminalGrammarSlot(")
		           .append(nonterminal.getConstructorCode(registry))
		           .append(")").toString();
	}
	
	@Override
	public GSSNode getGSSNode(int inputIndex) {
		return nodeLookup.getOrElseCreate(this, inputIndex);
	}
	
	@Override
	public GSSNode hasGSSNode(int inputIndex) { 
		return nodeLookup.get(inputIndex);
	}

	@Override
	public boolean isFirst() {
		return true;
	}
	
	public NonterminalNode getNonterminalNode(NonterminalNode key, NodeAddedAction<NonterminalNode> action) {
		NonterminalNode val;
		if ((val = nonterminalNodes.get(key)) == null) {
			val = key;
			action.execute(val);
			nonterminalNodes.put(key, val);
		}
		return val;
	}
	
	public NonterminalNode findNonterminalNode(NonterminalNode node) {
		return nonterminalNodes.get(node);
	}

	@Override
	public void reset(Input input) {
		nodeLookup.reset(input);
		nonterminalNodes = new HashMap<>();
	}

}
