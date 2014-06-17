package org.jgll.parser;


import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.lexer.GLLLexer;
import org.jgll.lexer.GLLLexerImpl;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.parser.lookup.factory.DescriptorLookupFactory;
import org.jgll.parser.lookup.factory.GSSLookupFactory;
import org.jgll.parser.lookup.factory.SPPFLookupFactory;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.ParseStatistics;
import org.jgll.util.hashing.HashTableFactory;
import org.jgll.util.logging.LoggerWrapper;

/**
 * GLLParser is the abstract base class for all the generated GLL parsers.
 * This class provides common methods for SPPF and GSS construction.
 * 
 * Based on the paper "GLL: parse tree generation."
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */
public abstract class AbstractGLLParserImpl implements GLLParser {
		
	protected static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractGLLParserImpl.class);
	
	static {		
		HashTableFactory.init(HashTableFactory.OPEN_ADDRESSING);
	}
	
	/**
	 * u0 is the bottom of the GSS.
	 */
	protected static final GSSNode u0 = GSSNode.U0;

	protected GSSLookup gssLookup;
	
	protected SPPFLookup sppfLookup;
	
	protected DescriptorLookup descriptorLookup;

	/**
	 * u0 is the bottom of the GSS.
	 */
	protected GSSNode cu = u0;
	
	protected SPPFNode cn = DummyNode.getInstance();
	
	protected int ci = 0;
	
	protected Input input;
	
	protected GLLLexer lexer;
	
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

	private ParseStatistics parseStatistics;

	public AbstractGLLParserImpl(GSSLookupFactory gssLookupFactory, 
								 SPPFLookupFactory sppfLookupFactory, 
								 DescriptorLookupFactory descriptorLookupFactory) {
		this.gssLookupFactory = gssLookupFactory;
		this.sppfLookupFactory = sppfLookupFactory;
		this.descriptorLookupFactory = descriptorLookupFactory;
	}
	
	@Override
	public ParseResult parse(Input input, GrammarGraph grammar, String startSymbolName) {
		return parse(new GLLLexerImpl(input, grammar), grammar, startSymbolName);
	}
	
	/**
	 * Parses the given input string. If the parsing of the input was successful,
	 * the root of SPPF is returned.
	 * 
	 * @param input the input string to be parsed.
	 * @return the SPPF root resulting from parsing the input
	 * 
	 * @throws ParseError a {@link ParseError} if the descriptor set is empty, but
	 * 								  no SPPF root has been found.
	 * @throws RuntimeException if no nonterminal with the given start symbol name is found.
	 */
	@Override
	public final ParseResult parse(GLLLexer lexer, GrammarGraph grammar, String startSymbolName) {
		
		HeadGrammarSlot startSymbol = grammar.getHeadGrammarSlot(startSymbolName);
		
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + startSymbolName + " found");
		}
		
		GSSNode.U0.clearDescriptors();
		
		this.grammar = grammar;
		
		this.lexer = lexer;
		
		this.input = lexer.getInput();
		
		initParserState();
		initLookups(grammar, input);
	
		log.info("Parsing %s:", input.getURI());

		long start = System.nanoTime();
		long startUserTime = getUserTime();
		long startSystemTime = getSystemTime();
		
		NonterminalSymbolNode root;
		
		L0.getInstance().parse(this, lexer, startSymbol);			
		root = sppfLookup.getStartSymbol(startSymbol, input.length());

		ParseResult parseResult;
		
		long end = System.nanoTime();
		long endUserTime = getUserTime();
		long endSystemTime = getSystemTime();
		
		if (root == null) {
			parseResult = new ParseError(errorSlot, this.input, errorIndex, errorGSSNode);
			log.info("Parse error:\n %s", parseResult);
		} else {
			parseStatistics = new ParseStatistics(input, end - start,
					  endUserTime - startUserTime,
					  endSystemTime - startSystemTime, 
					  getMemoryUsed(),
					  descriptorLookup.getDescriptorsCount(), 
					  gssLookup.getGSSNodesCount(), 
					  gssLookup.getGSSEdgesCount(), 
					  sppfLookup.getNonterminalNodesCount(), 
					  sppfLookup.getIntermediateNodesCount(), 
					  sppfLookup.getPackedNodesCount(), 
					  root.getAmbiguousNodes());

			parseResult = new ParseSuccess(root, parseStatistics);
			log.info("Parsing finished successfully.");			
			log.info(parseStatistics.toString());
		}
		
		return parseResult;
	}

	private int getMemoryUsed() {
		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		int memoryUsed = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
		return memoryUsed;
	}
		
	@Override
	public ParseStatistics getParseStatistics() {
		return parseStatistics;
	}
	
	public static long getUserTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        bean.getCurrentThreadUserTime() : 0L;
	}
	
	public long getSystemTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime( )) : 0L;
	}

	
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
			log.debug("Error recorded at %s %d", this, ci);
			this.errorIndex = ci;
			this.errorSlot = slot;
			this.errorGSSNode = cu;
		}
	}

	private void initParserState() {
		cu = u0;
		cn = DummyNode.getInstance();
		ci = 0;
		errorSlot = null;
		errorIndex = 0;
		errorGSSNode = null;
	}
	
	private void initLookups(GrammarGraph grammar, Input input) {
		gssLookup = gssLookupFactory.createGSSLookupFactory(grammar, input);
		sppfLookup = sppfLookupFactory.createSPPFLookup(grammar, input);
		descriptorLookup = descriptorLookupFactory.createDescriptorLookup(grammar, input);
	}

	/**
	 * Corresponds to the add method from the paper:
	 * 
	 * add(L, u, i, w) { 
	 * 	if(L, u, w) in ui { 
	 * 		add (L, u, w) to ui
	 * 		 add (L, u, i, w) to R 
	 *   } 
	 * }
	 */
	@Override
	public final void scheduleDescriptor(Descriptor descriptor) {
		descriptorLookup.scheduleDescriptor(descriptor);
	}
	
	@Override
	public void addDescriptor(GrammarSlot label) {
		scheduleDescriptor(new Descriptor(label, cu, ci, DummyNode.getInstance()));
	}	
	
	/**
	 * Pops the current element from GSS. When the top element, cu, is popped, there
	 * may be possibly n children. For each child u, reachable via an edge labelled
	 * by an SPPF node w, a new descriptor is added to the set of descriptors
	 * to be processed.
	 * 
	 * pop(u, i, z) { 
	 * 	if (u != u0) { 
	 * 		let (L, k) be the label of u 
	 * 		add (u, z) to P 
	 * 		for each edge (u, w, v) { 
	 * 			let y be the node returned by getNodeP(L, w, u)
	 * 			add(L, v, i , y)) 
	 *      } 
	 * }
	 */
	@Override
	public final GrammarSlot pop() {
		return pop(cu, ci, (NonPackedNode) cn);
	}
	
	protected abstract GrammarSlot pop(GSSNode gssNode, int inputIndex, NonPackedNode node);

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
	
	@Override
	public int getCurrentInputIndex() {
		return ci;
	}
	
	@Override
	public GSSNode getCurrentGSSNode() {
		return cu;
	}

	@Override
	public GSSLookup getGSSLookup() {
		return gssLookup;
	}
	
	public SPPFLookup getSPPFLookup() {
		return sppfLookup;
	}
	
	public DescriptorLookup getDescriptorLookup() {
		return descriptorLookup;
	}

	@Override
	public GrammarGraph getGrammar() {
		return grammar;
	}

	@Override
	public SPPFNode getCurrentSPPFNode() {
		return cn;
	}

	@Override
	public void setCurrentSPPFNode(SPPFNode node) {
		this.cn = node;
	}

	@Override
	public Descriptor getCurrentDescriptor() {
		return currentDescriptor;
	}
	
	@Override
	public TokenSymbolNode getTokenNode(int tokenID, int inputIndex, int length) {
		ci += length;
		return sppfLookup.getTokenSymbolNode(tokenID, inputIndex, length);
	}
	
}