package org.iguana.grammar.slot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.parser.gss.lookup.GSSNodeLookup;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.Input;
import org.iguana.util.collections.Key;


/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends AbstractGrammarSlot {
	
	private final Nonterminal nonterminal;
	
	private final List<BodyGrammarSlot> firstSlots;
	
	private final GSSNodeLookup nodeLookup;

	private Map<Key, NonterminalNode> nonterminalNodes;

	public NonterminalGrammarSlot(int id, Nonterminal nonterminal, GSSNodeLookup nodeLookup) {
		super(id);
		this.nonterminal = nonterminal;
		this.nodeLookup = nodeLookup;
		this.firstSlots = new ArrayList<>();
		this.nonterminalNodes = new HashMap<>();
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
	
	public String[] getParameters() {
		return nonterminal.getParameters();
	}
	
	@Override
	public String toString() {
		return nonterminal.toString();
	}
	
	@Override
	public String getConstructorCode() {
		return new StringBuilder()
		           .append("new NonterminalGrammarSlot(")
		           .append(nonterminal.getConstructorCode())
		           .append(")").toString();
	}
	
	@Override
	public GSSNode getGSSNode(int inputIndex) {
		return nodeLookup.getOrElseCreate(this, inputIndex);
	}
	
	@Override
	public GSSNode hasGSSNode(int inputIndex) { 
		if (nodeLookup.isInitialized()) {
			return nodeLookup.get(inputIndex);
		} else {
			nodeLookup.init();
			return null;
		}
	}

	@Override
	public boolean isFirst() {
		return true;
	}
	
	public NonterminalNode getNonterminalNode(Key key, Supplier<NonterminalNode> s, Consumer<NonterminalNode> c) {
		return nonterminalNodes.computeIfAbsent(key, k -> { NonterminalNode val = s.get();
															c.accept(val);
															return val; 
														  });
		}
	
	public NonterminalNode findNonterminalNode(Key key) {
		return nonterminalNodes.get(key);
	}
	
	public Iterable<GSSNode> getGSSNodes() {
		return nodeLookup.getNodes();
	}

	@Override
	public void reset(Input input) {
		nodeLookup.reset(input);
		nonterminalNodes = new HashMap<>();
	}
	
	public void initGSSLookup() {
		nodeLookup.init();
	}

	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public <T> GSSNode getGSSNode(int inputIndex, GSSNodeData<T> data) {
		return nodeLookup.getOrElseCreate(this, inputIndex, data);
	}
	
	@Override
	public <T> GSSNode hasGSSNode(int inputIndex, GSSNodeData<T> data) {
		if (nodeLookup.isInitialized()) {
			return nodeLookup.get(inputIndex, data);
		} else {
			nodeLookup.init();
			return null;
		}
	}
}
