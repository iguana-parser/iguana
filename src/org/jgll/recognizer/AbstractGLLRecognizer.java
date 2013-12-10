package org.jgll.recognizer;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.grammar.slot.StartSlot;
import org.jgll.lexer.GLLLexerImpl;
import org.jgll.recognizer.lookup.Lookup;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

public abstract class AbstractGLLRecognizer implements GLLRecognizer {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractGLLRecognizer.class);
	
	protected static final GrammarSlot startSlot = new StartSlot("Start");
	
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
	
	private Lookup lookup;
	
	/**
	 * The nonterminal from which the parsing will be started.
	 */
	protected HeadGrammarSlot startSymbol;
	
	protected boolean recognized;
	
	protected int endIndex;
	
	
	public AbstractGLLRecognizer(Lookup lookup, Grammar grammar) {
		this.lookup = lookup;
		this.grammar = grammar;
	}
	
	@Override
	public final boolean recognize(Input input, Grammar grammar, String nonterminalName) {
		
		if(grammar == null) {
			throw new RuntimeException("Grammar cannot be null.");
		}

		if(input == null) {
			throw new RuntimeException("Input cannot be null.");
		}
		
		HeadGrammarSlot startSymbol = grammar.getNonterminalByName(nonterminalName);
		
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + nonterminalName + " found");
		}
		
		init(grammar, input, 0, input.size() - 1, startSymbol);
		
		cu = create(startSlot, cu, ci);
	
		long start = System.nanoTime();
	
		L0.getInstance().recognize(this, new GLLLexerImpl(input, grammar), startSymbol);
		
		long end = System.nanoTime();

		logStatistics(end - start);

		return recognized;
	}
	
	@Override
	public boolean recognize(Input input, int startIndex, int endIndex, BodyGrammarSlot fromSlot) {
				
		init(grammar, input, startIndex, endIndex, null);
		
		cu = create(startSlot, cu, ci);
		
		long start = System.nanoTime();
		
		add(fromSlot, cu, ci);
		
		L0.getInstance().recognize(this, new GLLLexerImpl(input, grammar));
		
		long end = System.nanoTime();

		logStatistics(end - start);

		return recognized;
	}
	
	protected void logStatistics(long duration) {
		log.debug("Recognition Time: %d ms", duration/1000000);
		
		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		log.debug("Memory used: %d mb", (runtime.totalMemory() - runtime.freeMemory()) / mb);
		log.debug("Descriptors: {}", lookup.getDescriptorsCount());
		log.debug("GSS Nodes: {}", lookup.getGSSNodesCount());
		log.debug("GSS Edges: {}", lookup.getGSSEdgesCount());
	}
	
	/**
	 * initialized the parser's state before a new parse.
	 */
	protected void init(Grammar grammar, Input input, int startIndex, int endIndex, HeadGrammarSlot startSymbol){
		this.startSymbol = startSymbol;
		this.input = input;
		this.ci = startIndex;
		this.endIndex = endIndex;
		this.recognized = false;
		this.cu = u0;
		lookup.init(input);
	}

	@Override
	public void add(GrammarSlot slot, GSSNode u, int inputIndex) {		
		Descriptor descriptor = new Descriptor(slot, u, inputIndex);
		lookup.addDescriptor(descriptor);
	}
	
	@Override
	public final void pop(GSSNode u, int i) {
		
		log.trace("Pop %s, %d", u.getGrammarSlot(), i);
		
		if (u != u0) {
			
			// Add (u, i) to P
			u.addPoppedIndex(i);
			
			for(GSSNode node : u.getChildren()) {
				add(u.getGrammarSlot(), node, i);
			}			
		}
	}

	@Override
	public final GSSNode create(GrammarSlot slot, GSSNode currentGSSNode, int inputIndex) {
		
		GSSNode v = lookup.getGSSNode(slot, inputIndex);
		
		if(!v.hasChild(currentGSSNode)) {
			v.addChild(currentGSSNode);
			for(int index : v.getPoppedIndices()) {
				add(slot, currentGSSNode, index);
			}
		}
		
		return v;
	}
	
	@Override
	public boolean hasNextDescriptor() {
		return lookup.hasDescriptor();
	}
	
	@Override
	public Descriptor nextDescriptor() {
		return lookup.nextDescriptor();
	}
	
	@Override
	public void update(GSSNode gssNode, int inputIndex) {
		this.ci = inputIndex;
		this.cu = gssNode;
	}
	
	@Override
	public void recognitionError(GSSNode gssNode, int inputIndex) {
		
	}
	
	@Override
	public int getCi() {
		return ci;
	}
	
	@Override
	public GSSNode getCu() {
		return cu;
	}

}
