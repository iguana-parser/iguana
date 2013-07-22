package org.jgll.recognizer;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.L0;
import org.jgll.util.Input;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.logging.LoggerWrapper;

public abstract class AbstractGLLRecognizer implements GLLRecognizer {
	
	private static final int FULL = 0;
	
	private static final int PREFIX = 1;
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractGLLRecognizer.class);
	
	/**
	 * u0 is the bottom of the GSS.
	 */
	protected static final GSSNode u0 = GSSNode.U0;

	
	protected Input input;
	
	/**
	 * The current input index.
	 */
	protected int ci;

	/**
	 * The current GSS node.
	 */
	protected GSSNode cu = u0;

	/**
	 * 
	 */
	protected Grammar grammar;
	
	protected static final GrammarSlot startSlot = new StartSlot("Start");
	
	/**
	 * The nonterminal from which the parsing will be started.
	 */
	protected HeadGrammarSlot startSymbol;
	
	protected CuckooHashSet<Descriptor> descriptorSet;
	
	protected Deque<Descriptor> descriptorStack;
	
	protected CuckooHashSet<GSSNode> gssNodes;
	
	private boolean recognized;
	
	private int mode = FULL;
	
	private int endIndex = 0;
	
	@Override
	public final boolean recognize(Input input, Grammar grammar, String nonterminalName) {		
		HeadGrammarSlot startSymbol = grammar.getNonterminalByName(nonterminalName);
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + nonterminalName + " found");
		}
		
		init(grammar, input, 0, input.size() - 1, startSymbol);
		
		cu = create(startSlot, cu, ci);
	
		long start = System.nanoTime();
	
		recognize(startSymbol);
		
		long end = System.nanoTime();

		logStatistics(end - start);

		return recognized;
	}
	
	@Override
	public boolean recognizePrefix(Input input, Grammar grammar, String nonterminalName) {
		mode = PREFIX;
		return recognize(input, grammar, nonterminalName);
	}
	
	@Override
	public boolean recognize(Input input, int startIndex, int endIndex, BodyGrammarSlot slot) {
		init(grammar, input, startIndex, endIndex, null);
		
		cu = create(startSlot, cu, ci);
		
		long start = System.nanoTime();
		
		add(slot, cu, ci);
		
		L0.getInstance().recognize(this, input);
		
		long end = System.nanoTime();

		logStatistics(end - start);

		return recognized;
	}
	
	@Override
	public boolean recognizePrefix(Input input, int inputIndex, BodyGrammarSlot slot) {
		mode = PREFIX;
		return recognize(input, inputIndex, input.size() - 1, slot);
	}

	
	private void logStatistics(long duration) {
		log.info("Parsing Time: %d ms", duration/1000000);
		
		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		log.info("Memory used: %d mb", (runtime.totalMemory() - runtime.freeMemory()) / mb);
//		log.debug("Descriptors: {}", lookupTable.getDescriptorsCount());
//		log.debug("GSSNodes: {}", lookupTable.getGSSNodes().size());
//		log.debug("Non-packed nodes: {}", lookupTable.getDescriptorsCount());
//		log.debug("GSS Nodes: {}", lookupTable.getGSSNodesCount());
//		log.debug("GSS Edges: {}", lookupTable.getGSSEdgesCount());
	}

	
	protected abstract void recognize(HeadGrammarSlot startSymbol);
	
	/**
	 * initialized the parser's state before a new parse.
	 */
	protected void init(Grammar grammar, Input input, int startIndex, int endIndex, HeadGrammarSlot startSymbol){
		
		this.grammar = grammar;
		this.startSymbol = startSymbol;
		this.input = input;
		ci = startIndex;
		this.endIndex = endIndex;
		recognized = false;
		cu = GSSNode.U0;
		
		if(descriptorSet == null) {
			descriptorSet = new CuckooHashSet<>();
		} else {
			descriptorSet.clear();
		}
		
		if(descriptorStack == null) {
			descriptorStack = new ArrayDeque<>();
		} else {
			descriptorStack.clear();
		}
		
		if(gssNodes == null) {
			gssNodes = new CuckooHashSet<>();
		} else {
			gssNodes.clear();
		}
	}

	@Override
	public final void add(GrammarSlot slot, GSSNode u, int inputIndex) {
		
		if(mode == FULL) {
			if(slot == startSlot && inputIndex == endIndex && u == u0) {
				recognized = true;
				descriptorStack.clear();
				return;
			}
		} else {
			if(slot == startSlot && u == u0) {
				recognized = true;
				descriptorStack.clear();
				return;
			}
		}
		
		Descriptor descriptor = new Descriptor(slot, u, inputIndex);
		if(descriptorSet.add(descriptor) == null) {
			log.trace("Descriptor added: %s : true", descriptor);
			descriptorStack.push(descriptor);
		} else {
			log.trace("Descriptor %s : false", descriptor);
		}
	}
	
	@Override
	public final void pop(GSSNode u, int i) {
		
		log.trace("Pop %s, %d", u.getLabel(), i);
		
		if (u != u0) {
			
			// Add (u, i) to P
			u.addPoppedIndex(i);
			
			for(GSSNode node : u.getChildren()) {
				add(u.getLabel(), node, i);
			}			
		}
	}

	@Override
	public final GSSNode create(GrammarSlot L, GSSNode u, int i) {
		log.trace("GSSNode created: (%s, %d)",  L, i);
		GSSNode key = new GSSNode(L, i);

		GSSNode v = gssNodes.add(key);
		if(v == null) {
			v = key;
		}
		
		if(!v.hasChild(u)) {
			v.addChild(u);
			for(int index : v.getPoppedIndices()) {
				add(L, u, index);
			}
		}
		
		return v;
	}
	
	@Override
	public boolean hasNextDescriptor() {
		return !descriptorStack.isEmpty();
	}
	
	@Override
	public Descriptor nextDescriptor() {
		return descriptorStack.pop();
	}
	
	@Override
	public void update(GSSNode gssNode, int inputIndex) {
		this.ci = inputIndex;
		this.cu = gssNode;
	}
	
	@Override
	public void recognitionError(GSSNode gssNode, int inputIndex) {
		
	}

}
