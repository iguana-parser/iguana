package org.jgll.parser;


import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.lexer.Lexer;
import org.jgll.lexer.LexerImpl;
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
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
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
	
	protected SPPFNode cn = DummyNode.getInstance();
	
	protected int ci = 0;
	
	protected Input input;
	
	protected Lexer lexer;
	
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
		this.lexer = new LexerImpl(input, grammar);
		
		HeadGrammarSlot startSymbol = getStartSymbol(startSymbolName);
		
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
	
	protected HeadGrammarSlot getStartSymbol(String name) {
		return grammar.getHeadGrammarSlot(Nonterminal.withName(name));
	}
	
	protected void parse(HeadGrammarSlot startSymbol) {

		if(!startSymbol.test(lexer.getInput().charAt(ci))) {
			recordParseError(startSymbol);
			return;
		}
		
		GrammarSlot slot = startSymbol.parse(this, lexer);
		
		while(slot != null) {
			slot = slot.parse(this, lexer);
		}
		
		L0.getInstance().parse(this, lexer);
	}
	
	protected abstract void initParserState(HeadGrammarSlot startSymbol);
	
	@Override
	public GrammarSlot create(BodyGrammarSlot slot, HeadGrammarSlot head) {
		
		GSSNode gssNode = hasGSSNode(slot, head);
		if (gssNode == null) {
			log.trace("GSSNode created: %s",  cu);
			gssNode = createGSSNode(slot, head);
			createGSSEdge(slot, cu, cn, gssNode);
			cu = gssNode;
			return head;
		}
		
		log.trace("GSSNode found: %s",  gssNode);
		return createGSSEdge(slot, cu, cn, gssNode);
	}
	
	public abstract GrammarSlot createGSSEdge(BodyGrammarSlot returnSlot, GSSNode destination, SPPFNode w, GSSNode source);
	
	public abstract GSSNode createGSSNode(BodyGrammarSlot returnSlot, HeadGrammarSlot head);
	
	public abstract GSSNode hasGSSNode(BodyGrammarSlot slot, HeadGrammarSlot head);

	
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
	public TokenSymbolNode getTokenNode(TerminalGrammarSlot slot, int inputIndex, int length) {
		ci += length;
		return sppfLookup.getTokenSymbolNode(slot, inputIndex, length);
	}
	
}