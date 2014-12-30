package org.jgll.parser;


import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.EndGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.parser.lookup.factory.DescriptorLookupFactory;
import org.jgll.parser.lookup.factory.GSSLookupFactory;
import org.jgll.parser.lookup.factory.SPPFLookupFactory;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.BenchmarkUtil;
import org.jgll.util.Input;
import org.jgll.util.ParseStatistics;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * 
 * @author Ali Afroozeh
 * 
 */
public abstract class AbstractGLLParserImpl implements GLLParser {
		
	protected static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractGLLParserImpl.class);
	
	protected GSSLookup gssLookup;
	
	protected SPPFLookup sppfLookup;
	
	protected DescriptorLookup descriptorLookup;

	protected GSSNode cu;
	
	protected NonPackedNode cn = DummyNode.getInstance();
	
	protected int ci = 0;
	
	protected Input input;
	
	/**
	 * 
	 */
	protected GrammarGraph grammar;
	
	protected Descriptor currentDescriptor;
	
	/**
	 * The grammar slot at which a parse error is occurred. 
	 */
	protected GrammarSlot errorSlot;
	
	/**
	 * The last input index at which an error is occurred. 
	 */
	protected int errorIndex;
	
	/**
	 * The current GSS node at which an error is occurred.
	 */
	protected GSSNode errorGSSNode;
	
	protected GSSLookupFactory gssLookupFactory;

	protected SPPFLookupFactory sppfLookupFactory;

	protected DescriptorLookupFactory descriptorLookupFactory;
	
	protected int descriptorsCount;

	public AbstractGLLParserImpl(GSSLookupFactory gssLookupFactory, 
								 SPPFLookupFactory sppfLookupFactory, 
								 DescriptorLookupFactory descriptorLookupFactory) {
		this.gssLookupFactory = gssLookupFactory;
		this.sppfLookupFactory = sppfLookupFactory;
		this.descriptorLookupFactory = descriptorLookupFactory;
	}
	
	@Override
	public final ParseResult parse(Input input, GrammarGraph grammar, String startSymbolName) {

		this.grammar = grammar;
		this.input = input;
		
		NonterminalGrammarSlot startSymbol = getStartSymbol(startSymbolName);
		
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + startSymbolName + " found");
		}
		
		initLookups(grammar, input);

		initParserState(startSymbol);
	
		log.info("Parsing %s:", input.getURI());

		long start = System.nanoTime();
		long startUserTime = BenchmarkUtil.getUserTime();
		long startSystemTime = BenchmarkUtil.getSystemTime();
		
		NonterminalNode root;
		
		parse(startSymbol);
		
		root = sppfLookup.getStartSymbol(startSymbol, input.length());

		ParseResult parseResult;
		
		long end = System.nanoTime();
		long endUserTime = BenchmarkUtil.getUserTime();
		long endSystemTime = BenchmarkUtil.getSystemTime();
		
		if (root == null) {
			parseResult = new ParseError(errorSlot, this.input, errorIndex, errorGSSNode);
			log.info("Parse error:\n %s", parseResult);
		} else {
			ParseStatistics parseStatistics = new ParseStatistics(input, end - start,
					  endUserTime - startUserTime,
					  endSystemTime - startSystemTime, 
					  BenchmarkUtil.getMemoryUsed(),
					  descriptorsCount, 
					  gssLookup.getGSSNodesCount(), 
					  gssLookup.getGSSEdgesCount(), 
					  sppfLookup.getNonterminalNodesCount(),
					  sppfLookup.getTokenNodesCount(),
					  sppfLookup.getIntermediateNodesCount(), 
					  sppfLookup.getPackedNodesCount(), 
					  sppfLookup.getAmbiguousNodesCount());

			parseResult = new ParseSuccess(root, parseStatistics);
			log.info("Parsing finished successfully.");			
			log.info(parseStatistics.toString());
		}
		
		return parseResult;
	}
	
	protected NonterminalGrammarSlot getStartSymbol(String name) {
		return grammar.getRegistry().getHead(Nonterminal.withName(name));
	}
	
	protected void parse(NonterminalGrammarSlot startSymbol) {
		
		if(!startSymbol.test(input.charAt(ci))) {
			recordParseError(startSymbol);
			return;
		}
		
		startSymbol.execute(this, cu, ci, cn);
		
		while(hasNextDescriptor()) {
			Descriptor descriptor = nextDescriptor();
			GrammarSlot slot = descriptor.getGrammarSlot();
			ci = descriptor.getInputIndex();
			cu = descriptor.getGSSNode();
			slot.execute(this, cu, ci, descriptor.getSPPFNode());
		}
	}
	
	protected abstract void initParserState(NonterminalGrammarSlot startSymbol);
	
	@Override
	public GSSNode create(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, GSSNode u, int i, NonPackedNode node) {
		GSSNode gssNode = hasGSSNode(returnSlot, nonterminal, i);
		if (gssNode == null) {
			gssNode = createGSSNode(returnSlot, nonterminal, i);
			log.trace("GSSNode created: %s",  gssNode);
			createGSSEdge(returnSlot, u, node, gssNode);
			nonterminal.execute(this, gssNode, i, node);
//			for (Transition t : nonterminal.getTransitions()) {
//				scheduleDescriptor(new Descriptor(t.destination(), gssNode, i, DummyNode.getInstance()));				
//			}
		} else {
			log.trace("GSSNode found: %s",  gssNode);
			createGSSEdge(returnSlot, u, node, gssNode);			
		}
		return gssNode;
	}
		
	public abstract void createGSSEdge(GrammarSlot returnSlot, GSSNode destination, NonPackedNode w, GSSNode source);
	
	public abstract GSSNode createGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i);
	
	public abstract GSSNode hasGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i);

	
	/**
	 * Replaces the previously reported parse error with the new one if the
	 * inputIndex of the new parse error is greater than the previous one. In
	 * other words, we throw away an error if we find an error which happens at
	 * the next position of input.
	 * 
	 */
	@Override
	public void recordParseError(GrammarSlot slot) {
		if (ci >= this.errorIndex) {
			log.debug("Error recorded at %s %d", slot, ci);
			this.errorIndex = ci;
			this.errorSlot = slot;
			this.errorGSSNode = cu;
		}
	}
	
	protected void initLookups(GrammarGraph grammar, Input input) {
		gssLookup = gssLookupFactory.createGSSLookup(grammar, input);
		sppfLookup = sppfLookupFactory.createSPPFLookup(grammar, input);
		descriptorLookup = descriptorLookupFactory.createDescriptorLookup(grammar, input);
	}

	@Override
	public final void scheduleDescriptor(Descriptor descriptor) {
		descriptorLookup.scheduleDescriptor(descriptor);
		log.trace("Descriptor created: %s", descriptor);
		descriptorsCount++;
	}
	
	@Override
	public boolean hasDescriptor(Descriptor descriptor) {
		return descriptorLookup.addDescriptor(descriptor);
	}	
	
	@Override
	public boolean hasNextDescriptor() {
		return descriptorLookup.hasNextDescriptor();
	}
	
	@Override
	public Descriptor nextDescriptor() {
		Descriptor descriptor = descriptorLookup.nextDescriptor();
		ci = descriptor.getInputIndex();
		cu = descriptor.getGSSNode();
		cn = descriptor.getSPPFNode();
		currentDescriptor = descriptor;
		log.trace("Processing %s", descriptor);
		return descriptor;
	}
	
	public SPPFLookup getSPPFLookup() {
		return sppfLookup;
	}
	
	public DescriptorLookup getDescriptorLookup() {
		return descriptorLookup;
	}

	@Override
	public Input getInput() {
		return input;
	}
	
	@Override
	public GrammarSlotRegistry getRegistry() {
		return grammar.getRegistry();
	}
	
	@Override
	public TerminalNode getEpsilonNode(int inputIndex) {
		return sppfLookup.getEpsilonNode(inputIndex);
	}
	
	@Override
	public NonterminalNode getNonterminalNode(EndGrammarSlot slot, NonPackedNode child) {
		return sppfLookup.getNonterminalNode(slot, child);
	}
	
	@Override
	public NonterminalNode getNonterminalNode(EndGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		return sppfLookup.getNonterminalNode(slot, leftChild, rightChild);
	}
	
	@Override
	public IntermediateNode getIntermediateNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		return sppfLookup.getIntermediateNode(slot, leftChild, rightChild);
	}
	
	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return sppfLookup.getTerminalNode(slot, leftExtent, rightExtent);
	}
}
